# 10_entity_graphs

### Цель

Научиться делать эффективные запросы при помощи JPA. 

### Теория

Java persistence API - прекрасная абстракция для быстрой реализации взаимодействия с базой данных. К сожалению довольно 
часто быстрая != эффективная. Не смотря на то что мы уже выяснили, что для получения скорости при работе с БД лучше 
сразу использовать простые обертки, такие как Spring JDBC Template или JOOQ (платный или бесплатный, если работа с opensource БД),
все-таки чаще всего нужно просто быстро что-нибудь сделать на JPA и не думать о скорости.

Активация работы с Spring JPA начинается с аннотации ```@EnableJpaRepositories``` (В Spring boot она ставится автоматически при
добавлении в зависимости соответствующего стартера). Главным интрфейсом в Spring Data JPA является интерфейс ```Repository``` 
и кучка других интерфейсов, наследующихся он него. Самым популярным и основными в работе из них являются  ```JpaRepository```. 
Этот интерфейс предоставляет целую пачку запросов - запрос одного/нескольких элемента, paging/sorting, удаление в виде Batch, 
поиск по примеру и т.д. прямо из коробки.
 
Киллер-фичей Spring Data JPA является автоматическая генерация запросов из имени метода. Такой подход позволяет не писать
SQL вообще! В Spring Data мы можем абсолютно легально написать:

```
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Stream<Employee> findAllAsStreamBy();
    Optional<Employee> findMaybeOneById(Long id);
    List<Employee> findTop2ByOrderByAgeDesc();
}
```
Каждый из этих запросов мало того, что валидный, так еще и отработает ровно так как мы ожидаем, т.е. вернет ответ сразу в 
виде стрима или ```Optional``` (Note: ```Stream``` доступен только если взаимодействие с ним происходит внутри транзакции. 
Это требования связано с необходимостью закрытия стрима до флаша persistence context). Также можно делать всякие странные 
штуки - типо найти top 2 записи, отсортированные по полю Age. Более подробно про генерацию запросов из имен методом можно 
почитить [здесь[1]](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation)

А подробности по поводу абстракции репозиториев есть [здесь[2](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories),
Еще вот [тут[3]](https://github.com/spring-projects/spring-data-examples/tree/master/jpa) можно посмотреть примеры работы с JPA.

И все бы выглядело прекрасно, но в JPA есть 1 фатальное НО. А именно - работа с ленивыми коллекциями.

#### Маппинг ленивых коллекций  

Пусть у нас есть некая сущность ```City```. В городе может быть 1 или более сущностей ```Department```, а в кажом из них 
1 или более ```Employee```. Получаем такую структуру.

 
```
@Entity
@Table(name = "CITY")
public class City {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private Set<Department> departments;
}
```
```
@Entity
@Table(name = "DEPARTMENT")
public class Department {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CITY_ID", nullable = true)
    private City city;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private Set<Employee> employees;
}
```
```
@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @GeneratedValue()
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;

    @Column(name = "AGE", nullable = true)
    private Long age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID", nullable = true)
    private Department department;

}
```

Здесь стоит обратить внимание на ленивую загрузку ```@ManyToOne(fetch = FetchType.LAZY)```. Обычно поумолчанию любые связанные 
сущности в Spring JPA грузят лениво, чтобы не делать лишних запросов.

Пусть у нас теперь есть задача - получить список всех сотрудников (```Employee```) из всех городов (```City```).
Мы пишем запрос 
```
    public String printAllEmfsInAllCities() {
        var city = this.cityRepository.findAll();

        var emps = city.stream().map(City::getDepartments)
                .flatMap(List::stream)
                .map(Department::getEmployees)
                .flatMap(List::stream)
                .map(Employee::getName)
                .collect(Collectors.joining(","));

        return emps;
    }
```
И конечно же он не работает. Так как у нас нет транзакции, а сущность ```Department``` внутри ```City``` еще не инициализорвана.

Фигня вопрос, говорим мы и ставим аннотацию ```@Transactional```.

```
    @Transactional
    public String printAllEmfsInAllCities() {
        var city = this.cityRepository.findAll();

        var emps = city.stream().map(City::getDepartments)
                .flatMap(Set::stream)
                .map(Department::getEmployees)
                .flatMap(Set::stream)
                .map(Employee::getName)
                .collect(Collectors.joining(","));

        return emps;
    }
```

И все действительно начинает работать. Но только мы только что создали проблему ```n+1``` запроса, так как если посмотреть 
в генерируемый SQL мы получим следующую картину:
```
1. Hibernate: select * from city 
2. Hibernate: select department from department where department.city_id=?
3. Hibernate: select from employee where employee.department_id=?
4. Hibernate: select from employee where employee.department_id=?
5. Hibernate: select department from department where department.city_id=?
6. Hibernate: select from employee where employee.department_id=?
7. Hibernate: select from employee where employee.department_id=?
```

Что же получается...
1. Сначала Hibernate вытягивает все сущности ```City``` из базы данных.
2. Затем в коде у нас идет стрим по всем департаментам. Для каждого из них Hibernate делает 1 запрос, чтобы вытащить
все ```Department```. Этот стрим преобразуется в стрим из департаментов .
3. Для каждого из департаментов Hibernate делает еще по 1 запросу, чтобы вытащить всех ```Employee```.

И в итоге получается 7 запросов вместо 1. Это так называемая проблема ```N+1``` запросов. Еще на 1 пример такой же проблемы можно
посмотреть [здесь[4]](https://medium.com/@gdprao/fixing-hibernate-n-1-problem-in-spring-boot-application-a99c38c5177d)

Это одна из причин нелюбви к JPA в Spring. У вас есть запрос, который вроде как работает, после того, как вы поставили 
```@Transactional```, но такой способ фикса хоть и является рабочим, он одновременно является фундаментально неправильный, 
а злоупотреблением им приводит к неэффективным программам.

Хочется, чтобы вещи, которые можно сделать в 1 запрос дейсвительно делались в 1 запрос. Как этого достичь? Есть довольно много
путей

##### HQL

JPA говорит: у вас должен быть язык запросов JPQL. Hibernate имееют такой язык под названием HQL. 
[Вот его документация[5]](https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html)

Мы можем написать наш запрос в виде HQL запроса.

```
@Query("SELECT c" +
    " FROM City c" +
    " LEFT JOIN FETCH c.departments d" +
    " LEFT JOIN FETCH d.employees e")
Set<City> findAllBy();
```

И этого хороший запрос, потому что такой запрос превратиться в SQL:

```
Hibernate: 
    select *
    from city 
    left outer join department on city.id=department.city_id 
    left outer join employee on department.id=employees.department_id
```

Магия Hibernate сама замапит все сущности в ```City```. Такой же запрос можно написать в виде ```NamedQuery```. 
Сначала определим ```NamedQuery``` над нашим ```City```.

```
@Entity
@Table(name = "CITY")
@NamedQuery(
        name = "city.departments.employees",
        query = "SELECT c" +
                " FROM City c" +
                " LEFT JOIN FETCH c.departments d" +
                " LEFT JOIN FETCH d.employees e"
)
public class City {
```

Затем вызовем его в репозитории
```
@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @Query(name = "city.departments.employees")
    Set<City> findAllBy();
}
```

##### Native query

Возможно нам захочется получить максимум скорости и спользовать ручное преобразование объектов вместо автоматического,
которое дает JPA/Hibernate. В таком случае конечно лучше использовать Spring JDBC Template / JOOQ / QueryDSL / etc, но
можно написать и нативный запрос.

Надо помнить что нативные запросы не имею мапить себя сами. Другими словами такой запрос
```
    @Query(value = "SELECT * FROM CITY " +
            "LEFT OUTER JOIN DEPARTMENT ON CITY.ID = DEPARTMENT.CITY_ID " +
            "LEFT OUTER JOIN EMPLOYEE on DEPARTMENT.ID = EMPLOYEE.DEPARTMENT_ID", nativeQuery = true)
    Set<City[]> findAllBy();
```

Создаст такой SQL:
```
select *
    from city 
    left outer join department on city.id=department.city_id 
    left outer join employee on department.id=employee.department_id
```

И казалось бы все хорошо. Но разыменование результата этого запроса не произойдет автоматически в сущность City. И в итоге мы 
получим ту же самую ситуацию, что и с ```N+1``` запросами, а именно

```
1. Hibernate: SELECT CITY.ID, CITY.NAME, DEPARTMENT.ID, DEPARTMENT.NAME, EMPLOYEE.ID, EMPLOYEE.NAME FROM CITY LEFT OUTER JOIN DEPARTMENT ON CITY.ID = DEPARTMENT.CITY_ID LEFT OUTER JOIN EMPLOYEE on DEPARTMENT.ID = EMPLOYEE.DEPARTMENT_ID
2. Hibernate: select department from department where department.city_id=?
3. Hibernate: select from employee where employee.department_id=?
4. Hibernate: select from employee where employee.department_id=?
5. Hibernate: select department from department where department.city_id=?
6. Hibernate: select from employee where employee.department_id=?
7. Hibernate: select from employee where employee.department_id=?
```

Но мы можем обработать их руками! Только надо будет еще дать выбираемым столбцам алиасы. Тогда наш запрос будет выглядеть так:
```
    @Query(value = "SELECT " +
            "CITY.ID as CITY_ID, CITY.NAME as CITY_NAME, " +
            "DEPARTMENT.ID as DEPARTMENT_ID, DEPARTMENT.NAME as DEPARTMENT_NAME, " +
            "EMPLOYEE.ID as EMPLOYEE_ID, EMPLOYEE.NAME as EMPLOYEE_NAME, EMPLOYEE.AGE as EMPLOYEE_AGE FROM CITY " +
            "LEFT OUTER JOIN DEPARTMENT ON CITY.ID = DEPARTMENT.CITY_ID " +
            "LEFT OUTER JOIN EMPLOYEE on DEPARTMENT.ID = EMPLOYEE.DEPARTMENT_ID", nativeQuery = true)
    List<Object[]> findAllBy();
```

И нам вернется массив каких-то объектов (Даже не ```ResultSet```!). Мы можем сначала превратить его в мапу из мап, а затем превратить в список ```City```
```
        Map<City, Map<Department, List<Employee>>> result = unstructuredCities.stream().collect(Collectors.groupingBy(
                entryAsCity ->
                        City.builder()
                                .id(bigIntegerAsLong(entryAsCity[0]))
                                .name((String) entryAsCity[1])
                                .build(),
                Collectors.groupingBy(
                        entryAsDepartment ->
                                Department.builder()
                                        .id(bigIntegerAsLong(entryAsDepartment[2]))
                                        .name((String) entryAsDepartment[3])
                                        .build(),
                        Collectors
                                .mapping(entryAsEmployee ->
                                                Employee.builder()
                                                        .id(bigIntegerAsLong(entryAsEmployee[4]))
                                                        .name((String) entryAsEmployee[5])
                                                        .age(bigIntegerAsLong(entryAsEmployee[6]))
                                                        .build(),
                                        Collectors.toList()))
        ));

        for (City city : result.keySet()) {
            var listOfDeps = new HashSet<Department>();
            for (Department department : result.get(city).keySet()) {

                var listOfEmps = new HashSet<Employee>();
                for (Employee employee : result.get(city).get(department)) {
                    employee.setDepartment(department);
                    listOfEmps.add(employee);

                }
                department.setCity(city);
                department.setEmployees(listOfEmps);
                listOfDeps.add(department);
            }
            city.setDepartments(listOfDeps);
        }

        var cities = result.keySet();
```

Это выглядит довольно страшно (и оно так и есть), но на самом деле здесь происходит довольно простое преобразование
1. Сначала мы говорим, что хотим получить мапу с ключами - ```City``` и значениями в виде мапы из департаментов на список Employee 
(Важно: необходимо, чтобы у ```City``` и ```Department``` были определены ```equals``` и ```hashCode```).
2. Затем говорим, что первый и второй аргумент возвращаемого из SQL результата является объектом ```City```. Это будет ключ
новой мапы
3. А значение - будет еще 1 группировка. На этот раз ключ новой мапы будет ```Department```, который конструируется из 2 и 3 
поля результата, а его значением будет список ```Employee```, который мы конструируем через ```Collectors.mapping``` из 3 
последних полей.

2/3 этого кода можно было бы избежать (Или спрятать в другое место), если бы мы возвращали не ```List<Object[]>```, а 
какой-нибудь ```List<CityDepEmpJoined>```, т.е. некий объект на который должны мапится значения из запроса (в JPA так можно делать)

Ну и разумеется ручное преобразование будет работать быстрее, чем автоматическое в ```Hibernate```. Правда, как и всегда, 
когда речь косается производительности - необходимо замерить результаты до и результаты после, чтобы убедиться, что 
наши "оптимизации" действительно работают.

##### EntityGraph

Суть: Можно дать Hibernate подсказки о том, какие ленивые сущности нужно грузить. Для этого используются 
```EntityGraph```. Его можно поставить прямо над методом, а можно сначала дать ему имя (тогда он называется ```NamedEntityGraph```, 
а лишь затем использовать). [Вот раздел доки[6]](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.entity-graph), где описаны эти графы.

Все что нам нужно это 
1. Опеределить ```NamedEntityGraph```, 
2. Определить в репозитории некий метод, который запрашивает все сущности из базы, т.к. над ним нам надо задать использование 
```EntityGraph```
3. Сказать методу из п. 2 использовать наш ```EntityGraph```


Берем ```NamedEntityGraph```
```
@NamedEntityGraphs({
        @NamedEntityGraph(name = "city.departments.employees", 
                attributeNodes = {
                        @NamedAttributeNode(value = "departments", subgraph = "departments.employees"),
                },
                subgraphs = {
                        @NamedSubgraph(name = "departments.employees",
                                attributeNodes = @NamedAttributeNode("employees")),
                }),
})
```

Создаем метод, запрашивающий все сущности
```
@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    @EntityGraph(value = "city.departments.employees", type = EntityGraph.EntityGraphType.FETCH)
    Set<City> findAllBy();
}
```

Запускаем тест и получаем запрос:
```
select *
    from city 
    left outer join department on city.id=department.city_id 
    left outer join employee on department.id=employee.department_id
```

Здесь все хорошо за исключением того, что в JPA 2.1. не поддерживаются запросы по еще более глубоким структурам (Например, 
нельзя было бы дальше запросить одну из сущностей, внутри ```Employee```. Такие запросы можно делать только через один из 
API, о которых речь пойдет ниже или через  ```@Query```)

##### Другие способы 

Есть и другие способы решить проблему ```N+1``` запроса. 
1. Использовать Criteria API через ```Entity Manager``` ([тут есть пример[7]](https://www.baeldung.com/spring-data-criteria-queries)) 
или [Specification API[8]](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications) 
или [QuerDSL[9]](http://www.querydsl.com/). Все эти API предоставляют возможности по созданию Join'ов в коде.
2. Писать на JDBC Template / JOOQ / etc

### Почитать

1. Создание SQL запроса по тексту метода https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
2. Документация https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
3. Официальные примеры Spring Data JPA https://github.com/spring-projects/spring-data-examples/tree/master/jpa
4. Пример N+1 запроса https://medium.com/@gdprao/fixing-hibernate-n-1-problem-in-spring-boot-application-a99c38c5177d
5. Документация HQL https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html
6. Entity Graph https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.entity-graph
7. Пример использования Criteria API https://www.baeldung.com/spring-data-criteria-queries
8. Specification API https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications
9. Query DSL http://www.querydsl.com/
 
### Задание

Дана структура из City -> Departments -> Employee, описанная выше. Необходимо реализовать запрос в ```CityRepository``` так,
чтобы он в 1 запрос возвращал список всех City с заполненным списком Department и заполненным список Employee внутри 
каждого Department.

Этот запрос должен работать за 1 раундтрип в БД (т.е. только 1 select). Note: логирование SQL уже включено. В ```application.yml```
есть параметр ```spring.jpa.show-sql: true```.

Быстро вызвать этот метод можно при помощи теста в ```CityServiceTest```
# 11_specification_api

### Цель

Научиться работать со Specification API.

### Теория

Как мы выяснили в предыдущей части, существует много способов делать SQL запросы. Существует приблизительно такое же 
количество способов делать неэффективные запросы. Но что, если нужно сделать мало того что эффективный запрос, так еще 
и запрос с динамической фильтрацией. Например: Пользователь указывает некие параметры фильтрации на сайте и соответственно
ожидает в ответ отфильтрованные определенным образом данные.

Подобные запросы можно делать на разных инстументах. Из них выделяют 3:

- [Criteria API[1]](https://www.baeldung.com/spring-data-criteria-queries). Это самый старый способ взаимодействия 
 с базой данной непосредственно через ```EntityManager```.
- [Specification API[2]](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications). Основанный 
на Criteria API упрощенный способ работы с ним.
- [QueryDSL[3]](http://www.querydsl.com/). Библиотека, похожая немного и на Criteria API и Specification API. Она 
 настолько хороша, что даже упоминается в документации к Spring Data JPA.

Мы же будем говорить сегодня о Specification API.

#### Specification API

Центральным интерфейсов в Specification API является интерфейс ```Specification```
```
public interface Specification<T> {
  Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
            CriteriaBuilder builder);
}
```

Так же Jpa репозиторий, который хочет работать со спецификациями, должен наследоваться от ```JpaSpecificationExecutor<T>```:
```
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, JpaSpecificationExecutor<Department> {

}
```

Этот интерфейс содержит методы для поискать по спецификации одного объекта, нескольких объектов или count. Также присутствуют 
методы для работы с пагинацией+спецификацией одновременно.

#### Как определять свои спецификации

Обычно создается некий класс, который содержит спецификации в качестве статических полей, кажое из которых является 
 реализацией интерфейса ```Specification<T>``` Например вот так:

```
public class EmployeeSpecifications {

    public static Specification<Employee> olderThen20 =
            (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("age"), 20);

    public static Specification<Employee> workInDepartmentFRIR =
            (root, query, criteriaBuilder) -> {

                var fetch = root.fetch("department", JoinType.LEFT);
                return criteriaBuilder.equal(joinWithDepartment.get("name"), "FRIR");
            };
}
```

Для имплементации в данном случае очень удобно использовать лямбда-выражения.

И затем можно использовать эти спецификации при обращении к репозиторию
```
@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> allOlderThen20() {
        return this.employeeRepository.findAll(EmployeeSpecifications.olderThen20);
    }

    public List<Employee> allInFRIR() {
        return this.employeeRepository.findAll(EmployeeSpecifications.workInDepartmentFRIR);
    }

    public List<Employee> allOlderThen20InFRIR() {
        return this.employeeRepository.findAll(EmployeeSpecifications.workInDepartmentFRIR
                .and(EmployeeSpecifications.olderThen20));
    }
}
```

Обратите внимание на последний метод. Спецификации можно соединять между собой через метды ```.and()``` или ```.or()```.

Учитывая, что спецификация - всего лишь имплементация некоторого класса, её можно собирать даже налету динамически во время 
запроса. Именно это предстоит сделать вам в тестовом задании :)

#### Особенности Specification API

Есть несколько вещей, о которых нужно помнить, при работе со SpecificationAPI:

Первая вещь, на которую следует обратить внимание - это explicit fetch в примере выше в методе```workInDepartmentFRIR``` 
Здесь имлпментация JPA от Hibernate подготовила нам подлянку. Без такого fetch'а все все еще БУДЕТ работать, 
но тогда мы получим cross-join:

Пример без fetch:
```
    public static Specification<Employee> workInDepartmentFRIR =
            (root, query, criteriaBuilder) -> {

                return criteriaBuilder.equal(root.get("department").get("name"), "FRIR");
            };
```

Генерирует запросы:
```
select *
 cross join department where employee.department_id=department.id and department.name=?
```

Т.е. если бы в Employee у нас было бы скажем 5 ленивых сущностей, некоторые из которых были бы коллекциямы, мы бы написали 
здоровенный cross-join (а это декартево произведение), который с легкостью может занимать и пару миллионов строк. Делать этого, 
разумеется, нет никакого смысла, посколько мы можем написать явный Fetch. (Можно конечно понадеется, что исполнитель запросов
на уровне БД достаточно умный, чтобы не держать прямо весь cross-join в памяти, и это даже скорее всего так и есть, 
но зачем надеется, если можно не косячить?)

Пример с fetch:
```
    public static Specification<Employee> workInDepartmentFRIR =
            (root, query, criteriaBuilder) -> {

                var fetch = root.fetch("department", JoinType.LEFT);
                return criteriaBuilder.equal(root.get("department").get("name"), "FRIR");
            };
```

Генерирует запросы:
```
select *
 left outer join department on employee.department_id=department.id where department.name=?
```

Ну и соответственно тип fetch можно заменить на INNER вместо LEFT, если точно известно, что не будет пользователь без департаментов.

Есть еще 1 особенность. У класс ```criteria.Root``` (Это первый аргумент в метода ```toPredicate``` интерфейса ```Specification<T>```) 
есть метод с названием ```join```. И его можно даже использовать вот так:
```
    public static Specification<Employee> workInDepartmentFRIR =
            (root, query, criteriaBuilder) -> {

                var join = root.join("department", JoinType.LEFT);
                return criteriaBuilder.equal(join.get("name"), "FRIR");
            };
```

Он создает, как это неудивительно, join с какой-то другой таблицей. Но это просто join остается только на уровне базы! 
При маппинге результата в нашу сущность ```Employee``` этот join не будет учитываться.

Запрос здесь будет получаться ровно такой же, как и с fetch:
```
select *
 left outer join department on employee.department_id=department.id where department.name=?
```

Но при таком подходе, сущность ```Department``` внутри сущности ```Employee``` не будет заполнена и мы получим 
```LazyInitializationException``` при попытке обратиться к пропертям класса Department внутри Employee (```emp.getDepartment().getName()```, 
например). Т.е. запрос с join делает этот самый join, но не парсит весь его результат в нужные сущности, а лишь собирает 
```root``` сущность - ```Employee```. Соответственно запросы с join можно использовать для повышения производительности, 
когда дочерние ленивые сущности в рамках запроса нам не нужны.

### Почитать

1. Criteria API https://www.baeldung.com/spring-data-criteria-queries
2. Specification API https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#specifications
3. QueryDSL http://www.querydsl.com/

### Задание


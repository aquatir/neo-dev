# 09_life_without_spring_data

### Цель

Научиться работать с БД чем-то кроме JPA.

### Теория


#### Введение в Spring JDBC Template
Как рассказывалось в предыдущей части, Spring JPA - не единственный способ работать с базой данных. Иногда от приложения
требуется получить максимум производительности и максимум гибкости. В таких случаях бывает полезным убрать все обертки, 
которые предоставляет Spring JPA и организовать взаимодействие с базой напрямую.

Но напрямую работать с Java в принципе геморно. Поэтому существует ```Spring JDBC Template```. Его суть - предоставление прямого 
доступ к JDBC из приложения на Spring, при этом работой с ```Datasource``` (конфигурация и менеджмент ```Connection pool```), 
работа с объектами ```PreparedStatement``` и ```Connections/Statement``` а также работа с транзакциями все еще будет 
осуществляться фрейморком. Таким образом, JDBC Template - это легковестная обвязка вокруг прямого доступа к возможностям 
JDBC драйвера, но на стероидах, за счет автоконфигурации всего, что можно сконфигурировать. В том числе и транзакции 
через ```@Transactional```.

#### Использование Spring JDBC Template

Использовать JDBC template со Spring Boot даже слишком просто. Для начала необходимо добавить зависимость
```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
```

Затем завайрить ```JdbcTemplate``` в один из своих компонент, например:
```
@Component
public class EmployeeDao {
    private final JdbcTemplate jdbcTemplate;
    
        public EmployeeDao(JdbcTemplate jdbcTemplate) {
            this.jdbcTemplate = jdbcTemplate;
        }
}
```

Note: как правило работу с БД при использовании JDBC Template раздлеяют на 2 класса. Первый - ```DAO```. Это класс, который всегда 
работает только с одной таблицей и делает только 1 запрос при вызове одного из своих методов. Второй - это ```Repository```. 
В ```Repository``` может хранится 1 или несколько ```DAO```. ```Repository``` разбивает сложные запросы к базе на несколько запросов 
в разные ```DTO``` при необходимости. Также, как правило, на уровне ```Repository``` хранятся "сложные" запросы, т.е.
запросы к нескольким базам сразу. Такое разделение может выглядеть следующим образом:

```
@Component
public class DepartmentRepository {
    private final EmployeeDao employeeDao;
    private final DepartmentDao departmentDao;
    
    public DepartmentRepository(EmployeeDao employeeDao, DepartmentDao departmentDao) {
        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
    }
        
    @Transactional(readOnly = true)
    public Department findOneEagerlyById(Long id) {
        var dep = this.findOneLazyById(id);
        var emps = this.employeeDao.findAllByDepartmentId(dep.getId());
        dep.setEmployees(emps);
        
        return dep;
    }    
```

Здесь мы создаем класс ```DepartmentRepository```, содержащий 2 DAO - ```EmployeeDao``` и ```DepartmentDao```, который использует
оба этих DAO для получения всех пользователей, находящихся в департаменте в методе ```findOneEagerlyById(Long id)```

Терминология с ```DTO``` и ```Repository``` не является общепринятой, а является исключительно выдумкой автора, зарекомендовавшей 
себя в работе :)

При этом, работу с транзакциями ```@Transactional``` можно выносить вообще на уровень Service'ов. Либо же оставлять на уровне
```Repository```, если для каких-то запросов строго необходимо специфичное поведение транзакции (транзакция внутри транзакции, 
например)

---
Получив объект ```JdbcTemplate``` в пользование, мы можем использовать один из его многочисленных методов для работы с данными. 
Рекомендую почитать Javadoc к этому классу, чтобы посмотреть, что он умеем. Да и код JDBC Template совершенно элементарный. 
Не зря он является легковестной обвеской. 

Как правило для Select будет использовать 1 из вариаций запросов:
```
public <T> List<T> query(String sql, RowMapper<T> rowMapper)
public <T> T queryForObject(String sql, @Nullable Object[] args, RowMapper<T> rowMapper)
```
Первый используется для получения списка результатов, а второй - для получения одного результата. 


Обновлять данные можно при помощи метода
```
public int update(String sql, @Nullable Object... args)
```

Note: JDBC Template практически не делает магии. Так как update запрос в обычном SQL возвращает количество измененных 
рядов - в JDBC Template он делает тоже самое. Ровно как и insert. Ровно как и delete. Это одна из причин, почему delete
и insert в частности делаются через метод update.

Вставку (Insert) можно делать по-разному. На выбор - это либо использовать того же самого метод ```update```, 
либо через упрощенный builder-класс-помощник ```SimpleJdbcInsert```.
```
    public Long insertNew(Employee employee) {
        var simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("EMPLOYEE")
                .usingGeneratedKeyColumns("ID");

        var params = new HashMap<String, Object>();
        params.put("NAME", employee.getName());

        Number id = simpleInsert.executeAndReturnKey(params);
        return id.longValue();
    }
```
 
Удаление делается также элементарно через ```update```, представленный выше (Из-за этого немного неочевидного именования, в 
частности, довольно удобно выделять Data Access Layer в отдельный слой).

Осталось рассмотреть последний класс - это ```RowMapper<T>``` - функциональный интерфейс, отвечающий за маппинг возвращенных 
из SQL запроса строк в объекты. Эта имплементация позволяет создавать объекты из результатов БД. Как правило для создания 
объектов из результатов БД проще всего использовать шаблон builder. Выглядит это например вот так:
```
public static RowMapper<Employee> ROW_MAPPER_LAZY = (rs, rowNum) -> Employee.builder()
    .id(rs.getLong("id"))
    .name(rs.getString("name"))
    .department(Department
        .builder()
        .id(rs.getLong("department_id"))
        .build())
    .build();
```

Следует также отметить, что имена колонок будут ровно такие же, как и при обычном SQL запросе. Иными словами - при использовании 
алиасов в запросе в БД, такие же алиасы стоит использовать при написании мапинга. Например:

```
    private final String FIND_ONE_BY_ID_WITH_DEPARTMENT = "SELECT * FROM EMPLOYEE as EMPLOYEE " +
        "JOIN DEPARTMENT as DEPARTMENT " +
        "ON (EMPLOYEE.DEPARTMENT_ID = DEPARTMENT.ID)" +
        " WHERE EMPLOYEE.ID = ?";
            
            
    public static RowMapper<Employee> ROW_MAPPER_WITH_DEPARTMENT = (rs, rowNum) -> Employee
        .builder()
        .id(rs.getLong("EMPLOYEE.ID"))
        .name(rs.getString("EMPLOYEE.NAME"))
        .department(Department
            .builder()
            .id(rs.getLong("DEPARTMENT.ID"))
            .name(rs.getString("DEPARTMENT.NAME"))
            .build())
        .build();
```

Здесь рекомендация следующая - договриться об именовании алиасов на берегу. Как правило алиас с таким же именем, как и 
имя сущности, работает более-менее хорошо.

Пример-введение на JDBC Template можно посмотреть [здесь[1]](https://spring.io/guides/gs/relational-data-access/) и
[здесь[2]](https://www.baeldung.com/spring-jdbc-jdbctemplate).

### Прочие возможности

На JDBC template можно исполнять любые запросы, которые можно исполнять на SQL. Например, DDL при помощи методов ```execute``` 
в JdbcTemplate. Или даже вызывать хранимые процедуры при помощи удобного билдера-помощника ```SimpleJdbcCall```.
```
SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
    .withSchemaName(schema)
    .withCatalogName(package)
    .withProcedureName(procedure)();
    
jdbcCall.execute(callParams);
```

Или совсем просто:
```
jdbcTemplate.update("call SOME_PROC (?, ?)", param1, param2);
```

Более подробно про работу с JDBC Template можно почитить в [официальной доке! [3]](https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#jdbc)

Дополнительно стоит сказать про транзакции и обработку ошибок. Как правило - достаточно знать только того, что обычные 
инстументы Spring (```@Transactional```, например и преобразование Checked ошибок JDBC) работает ровно также, как и всегда 
(т.е. автоматически). Тем не менее, если хочется управлять траназкциями вручную, можно 
[здесь[4]](https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction) почитать, 
как это делать.


На последок, еще раз скажу, что JPA - не единственный способ работы с базами данных. Более подробно про альтернативы JPA
можно почитать [здесь[5]](https://dzone.com/articles/jpa-hibernate-alternatives).

И еще 1 момент. JDBC template МОЖЕТ быть источником атаки [SQL Injection[6]](https://www.owasp.org/index.php/SQL_Injection). 
Поэтому НЕ СТОИТ создавать запросы на SQL самому на произвольных входных пользовательских данных. 
Всегда используйте ```PreparedStatement``` (А вернее одну из форм update/query/execute, которая использует 
внутри ```PreparedStatement```)

### Почитать

1. Пример-введение по JDBC Template #1 https://spring.io/guides/gs/relational-data-access/
2. Пример-введение по JDBC Template #2 https://www.baeldung.com/spring-jdbc-jdbctemplate 
3. Оф. дока про JDBC Template https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#jdbc
4. Менеджмент транзакций в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction
5. Альтернативы JPA еще раз https://dzone.com/articles/jpa-hibernate-alternatives
6. SQL injection https://www.owasp.org/index.php/SQL_Injection

### Задание

Даны классы ```Employee``` и ```Department```, их DAO - ```EmployeeDao``` и ```DepartmentDao``` и их репозитории -
 ```EmployeeRepository``` и ```DepartmentRepository```. 
 
 1. В ```DepartmentDao``` необходимо добавить реализацию метода ```findOneLazyById(Long id)```, который ищет 1 объект Department 
 без списка ```Employee```
 2. В ```EmployeeRepository``` необходимо добавить реализацию метода ```findOneEagerlyById(Long id)```, который ищет 1 
 объект ```Employee```, а также заполняет объект ```Department``` внутри этого ```Employee```. Note: здесь может быть 2 
 реализации - в 1 запрос с Join или в 2 запроса. Можно реализовать любую. Возможность делать более или менее оптимизированные 
 запросы (С точки зрения количество запроса, используемых индексов и т.д.) - одно из основных преимуществ ```JDBC Template```.
 
 Все тесты должны успешно проходить.
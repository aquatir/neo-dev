# 13_own_context_testcontainers

### Цель

Научиться создавать произвольный контекст для тестов. Научиться пользоваться testcontainers

### Теория

При тестировании существует фундаментальная проблема - что делать с другими зависимыми сервисами/приложениями?

Один из способов работы с ними является замена их на какой-то промежуточный вариант. Например реальную базу данных 
иногда меняют на in-memory базу, типо H2. Или внешний сервис берут и полностью мокают клиент к нему.

И хотя такой подход работает в большинстве случаев, иногда все-таки он может давать сбои. Крайне-крайне редко наша
H2 ведет себя не так, как Postgres, в мок нашего сервиса устарел и его реальная версия уже возвращает ответ в другом формате.

Для решения этой проблемы можно было бы использовать Docker. Тогда мы могли бы поднимать точные копии каких-то контейнеров, 
которые ровно в таком же виде будут доступны на production среде и тестироваться вместе с ними. Но тут возникает еще 1 проблема -
как менеджерить жизненный цикл этих контейнеров. Как их поднимать и оживлять? Именно с этой проблемой работает библиотека
testcontainers

#### Testcontainers

[Testcontainers[1]](https://www.testcontainers.org/) - это Java библиотека, поддерживающая Junit и Spring для управления 
жизненным циклом Docker контейнеров во время проведения тестирования. Она позволяет элементарным образом тестировать 
наше приложение не на каких-то там возможно устаревших моках, а на всамделешных инстансах сервисов, с которыми будет 
работать приложение на production среде. 

Testcontainers позволяет на время жизни теста (в рамках 1 процесса) поднять все необходимые инстансы, которые являются 
внешним миром для нашего приложения (Примеры: Postgres/Cassandra/Redis/Kafka/любые другие приложения, помещенные в докер image), 
провести полноценное интеграционное тестирование, а затем убить все эти инстансы.

Распространяется это чудо под MIT лицензией, так что бояться его использовать смысла нет. Если вы еще думаете, стоит ли
использовать testcontainers - вот есть [прекрасное видео[2]](https://vimeo.com/222501136) от создателя библиотеки, которое 
поможет расставить все точки над "И", и 100% её вам продать :)

#### Как начать работать с testcontainers

1. Скачать и установить docker. На Windows docker 18 версии точно работает. На Linux работает точно начиная с 16. На Маки 
у меня нет информации
2. Добавить зависимость на testcontainers
```
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <version>1.10.1</version>
            <scope>test</scope>
        </dependency>
```

Все! Теперь нам необходимо лишь сказать testcontainers, чтобы он запускал необходимые образы. Небольшая проблема заключается
в том, что иногда нужно получить от этих образов информацию, чтобы использовать её в качестве ```Environment``` пропертей 
для Spring Boot приложения. Но с этой проблемой нам поможет документацию по тестированию, а именно ```@ContextConfiguration```.

Пример: Нам нужно протестировать приложение на реальном postgres. Как бы это сделать?

Первое: создать свой класс - ApplicationContextInitializer. В нем запустить нужный контейнер и скормить нужные проперти 
файлы в ```Environment```. В примере обратите внимание на использовать ```TestPropertyValue``` хелпера, о котором мы 
уже говорили в прошлом выпуске.

```
public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    
    public static PostgreSQLContainer postgres =
            (PostgreSQLContainer) new PostgreSQLContainer("postgres:10.5")
                    .withDatabaseName("dev")
                    .withUsername("bai")
                    .withPassword("test-password")
                    .withStartupTimeout(Duration.ofSeconds(600))
                    .withExposedPorts(5432);    
    
    public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgres.getJdbcUrl(),
                "spring.datasource.username=" + postgres.getUsername(),
                "spring.datasource.password=" + postgres.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
```

Далее надо использовать этот initializer в реальном тесте:

```
@SpringBootTest
@ContextConfiguration(initializers = {PostgresContainerInitializer.class})
@RunWith(SpringRunner.class)

@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeRepositoryTest {

    @ClassRule
    public static PostgreSQLContainer postgres = PostgresContainerInitializer.postgres;

    @Autowired private EmployeeRepository employeeRepository;

    @Test
    public void test_findAll() {
        var emps = this.employeeRepository.findAll();
        assertEquals(4, emps.size());
    }
}
```

Note: Параметры контейнера можно задавать по-разному. В данном случае 

Это в принципе все. Наш тест будет работать cледующим образом:
1. Testcontainers поднимет указанный Docker образ ```postgres:10.5```
2. initializer загонит его параметры в ```spring.datasource.*```
3. На новую поднятую базу прольются скрипты миграции flyway (Соответственно ошибку в скриптах миграции вы тоже увидите сразу же
во время теста)
4. затем прольется SQL скрипт из ```@Sql```.
5. Запустятся и отработают все тесты
6. После окончания тестов, Testcontainers сам опустит образ с постгресом.

Можно запускать тесты немного по-другому, а именно все также использовать initializer, но в его коде вставлять статичный блок
```
    static {
        postgres.start();
    }
```

Тогда не будет необходимости указывать ```Postgres``` в ```@ClassRule```.

#### Что еще умеет testcontainers

Запускать postgres - это не единственная фича testcontainers. Помимо этого у библиотеки есть поддержка:

- Работа с MySQL, Oracle. Есть сторонние библиотеки для KV хранилищ, типо MongoDB, Cassandra.
- Работа с В теории, работать можно с любой 
другой базой, которую можно запихнуть в контейнер, только тогда, возможно, придется по образу и подобию написать несколько
классов для работы.
- Работа с Docker-compose файлами. Т.е. можно загрузить совершенно произвольное внешнее окружение
- UI тестирование с запуском реального браузера. Здесь же поддерживается запись видео.
- В принципе все что угодно, при имплементации ```GenericContainer```

Пример:
```
@ClassRule
public static GenericContainer redis =
    new GenericContainer("redis:3.0.2")
            .withExposedPorts(6379);
```

### Почитать

1. Testcontainers основная страница https://www.testcontainers.org/
2. Видео TestContainers – Richard North https://vimeo.com/222501136

### Задание

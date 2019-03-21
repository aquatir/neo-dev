# 12_basic_junit

### Цель

Научиться тестировать Spring Boot приложения. Разобраться с видами тестов.

### Теория

Spring Test имеет поддержку, как unit-тестов, так и интеграцинных тестов. С точки зрения Spring, юнит-тест - это тест, для 
работы которого не нужно поднятие контекста приложения. Здесь Spring предоставляет такие инструменты, как классы ```ReflectionTestUtils```
 и ```AopTestUtils / AopUtils / AopProxyUtils```. Первый упрощает работу с reflexion в тестах (изменение неизменяемых полей, 
 вызов private методов, все такое. Но следует помнить, что с точки зрения дизайна приложений очень часто необходимость 
 использовать рефлексию вызвана неправильным разделением ответственностей компонентов этого приложения). Второй позволяет
 работать со Spring AOP. Например получать доступ к Mock объектами, находящимися за Spring AOP проксями (CGLIB прокси, например).

Более подробро про unit тестирование в Spring можно почитать [здесь[1]](https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#unit-testing)

#### Интеграционное тестирование 

Но конечно же основная часть документации Spring посвящена интеграционному тестированию, т.е. тестирование с полноценным Spring контекстом.

Более того, имеет смысл изучать 2 документаци - документация самого [Spring[2]](https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#integration-testing) 
 и документация [Spring Boot про тестирование[3]](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html)

Главной "проблемой" тестирования Spring / Spring Boot приложения является необходимость конфигурации некоторого Application Context
при работе с тестом. Как правило можно брать самый простой вариант - т.е. конфигурирование всего контекста при помощи
аннотаций:
```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MyTestClass {

}
```
Такой подход работает всегда, но иногда хочется сконфигурировать только часть контекст, особенно если приложение очень 
большой. Сейчас мы рассмотрим, какие есть виды Spring Boot тестов. Делается это как правило, 
чтобы сэкономить время поднятия контекста. 

#### JSON тесты

Иногда хочется просто напросто проверить маппинги JSON'ов в объекты и объектов в JSON'ы. В таком случае тест следует 
определять через аннотацию ```@JsonTest```. Пусть у нас есть объект ```Employee```. Тогда тестирование его превращение
в JSON и обратно будет выглядеть так:

```
@RunWith(SpringRunner.class)
@JsonTest
public class EmployeeDtoTest {

    @Autowired
    private JacksonTester<EmployeeDto> json;

    private EmployeeDto employeeDto = EmployeeDto.builder()
            .id(1L)
            .name("testEmp")
            .age(5L)
            .department(
                    DepartmentDto.builder()
                            .id(1L)
                            .name("testDep")
                            .build()
            )
            .build();;


    @Test
    public void testSerialize() throws Exception {
        assertThat(this.json.write(this.employeeDto)).isEqualToJson("/jsons/employeeDto.json");
    }

    @Test
    public void testDeserialize() throws Exception {
        var jsonText = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"testEmp\",\n" +
                "  \"age\": 5,\n" +
                "  \"department\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"testDep\"\n" +
                "  }" +
                "}";

        var testEmp = this.json.parseObject(jsonText);
        assertThat(testEmp.getAge()).isEqualTo(this.employeeDto.getAge());
        assertThat(testEmp.getName()).isEqualTo(this.employeeDto.getName());
        assertThat(testEmp.getDepartment().getName()).isEqualTo(this.employeeDto.getDepartment().getName());
    }
}

```

Файл employeeDto.json содержит JSON с описанием ```employeeDto```:
```
{
  "id": 1,
  "name": "testEmp",
  "age": 5,
  "department": {
    "id": 1,
    "name": "testDep"
  }
}
```


#### Web MVC Тесты

Иногда хочется протестировать правильность маппингов на контроллерах и возможно их сериализацию в JSON. При этом сделать 
это хочется быстро, т.к. таких тестов много. Для этих целей в Spring Boot есть ```@WebMvcTest```, которая тестирует 
только 1 контроллер. При таком тестировании обычно имеет смысл закомать все сервисы, с которым взаимодействует данный.

При таком подходе, бины, например JPA не поднимаются. Все ответы нужно мокать. Ниже есть пример:


```
@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    public void test_AsMock_FindAll() throws Exception {
        given(this.employeeService.findAll())
                .willReturn(List.of(
                        Employee.builder()
                                .id(1L)
                                .name("test1")
                                .age(1L)
                                .build(),
                        Employee.builder()
                                .id(2L)
                                .name("test2")
                                .age(2L)
                                .build()));


        this.mvc.perform(get("/employee").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"name\":\"test1\",\"age\":1,\"department\":null},{\"id\":2,\"name\":\"test2\",\"age\":2,\"department\":null}]"));
    }

    @Test
    public void test_AsMock_FindOneById() throws Exception {
        given(this.employeeService.findById(anyLong()))
                .willReturn(
                        Optional.of(Employee.builder()
                                .id(1L)
                                .name("test1")
                                .age(1L)
                                .build())
                        );

        this.mvc.perform(get("/employee/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"test1\",\"age\":1,\"department\":null}"));
    }
}
```

Аналогичным образом можно делать ```WebFlux``` тесты. См [документацию[4]](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-webflux-tests)

#### JPA Тесты

аналогичным образом можно тестировать только JPA слой, не поднимаю web и прочие слои приложения. И тут тоже есть своя аннотация - 
```@DataJpaTest```.

```
@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void test_FindOne() {
        var maybeEmp = this.employeeRepository.findById(1L);
        assertTrue(maybeEmp.isPresent());
    }

    @Test
    public void test_findAll() {
        var emps = this.employeeRepository.findAll();
        assertEquals(4, emps.size());
    }
}
```

#### Прочие виды тестов

Существует еще целая куча разных аннотаций для различных видов тестирования:

- ```@JdbcTest``` - тестирования, когда необходима только настройка ```Datasource```. Работает с ```JdbcTemplate```.
- ```@DataJdbcTest``` - как пример выше, только еще конфигурируются Spring Data JDBC репозитории.
- ```@JooqTest``` - тестирование с ```Datasource``` при использовании JOOQ (он автоконфигурирует ```DSLContext```)
- ```@DataMongoTest``` - тестирование mongoDB
- ```@DataNeo4jTest``` - тестирование neo4fj
- ```@DataRedisTest``` - тестирование redis
- ```@DataLdapTest``` - тестирование LDAP (Ldap репозитории)
- ```@RestClientTest``` - позволяет быстро тестировать REST запросы. Использует ```MockRestServiceServer```. См. 
[пример[5]](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-rest-client)

#### Интересные фишки Junit + Spring boot тестов

##### Можно ловить аутпут тестов

```

public class Employee {

	@Rule
	public OutputCapture capture = new OutputCapture();

	@Test
	public void testName() throws Exception {
		System.out.println("Hello World!");
		assertThat(capture.toString(), containsString("World"));
	}

}
```

##### Можно добвлять проперти в Environment прямо в тесте

```
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
```

##### TestRestTemplate

В тестах можно использовать ```TestRestTemplate``` - это такой особы ```RestTemplate```, который не следует за редиректами
и игнорирует куки.

Его можно инстанцировать самостоятельно
```
private TestRestTemplate template = new TestRestTemplate();
```

Либо завайрить, если тестируется реальный web слой
```
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleWebClientTests {

	@Autowired
	private TestRestTemplate template;
}
```


### Почитать

1. Unit тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#unit-testing
2. Интеграционное тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#integration-testing
3. Тестирование в Spring Boot https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html
4. Тестирование WebFlux в Spring Boot https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-webflux-tests
5. Тестирование при помощи @RestClientTest https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-testing-autoconfigured-rest-client

### Задание

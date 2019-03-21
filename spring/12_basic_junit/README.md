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
Такой подход работает всегда, но иногда нужно конфигурировать контекст как-то по особенному. Сейчас мы рассмотрим, какие есть
виды Spring Boot тестов. Делается это как правило, чтобы сэкономить время поднятия контекста. 

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

Иногда хочется протестировать правильность маппингов на контроллерах. При этом сделать это быстро. Для этих целей в 
Spring Boot есть ```@WebMvcTest```, которая тестирует только 1 контроллер. При таком тестировании обычно имеет смысл закомать
все сервисы, с которым взаимодействует данный.





### Почитать

1. Unit тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#unit-testing
2. Интеграционное тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#integration-testing
3. Тестирование в Spring Boot https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html

### Задание

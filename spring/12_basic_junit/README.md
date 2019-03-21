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
виды Spring Boot тестов


#### JSON тесты

Иногда хочется просто напросто проверить маппинги JSON'ов в объекты и объектов и JSON'ы. В таком случае тест следует 
определять через аннотацию ```@JsonTest```. Пусть у нас есть объект ```Employee```. Тогда тестирование его превращение
в JSON и обратно будет выглядеть так:




#### 


Примеры тестов на Spring Web https://github.com/spring-projects/spring-framework/tree/master/spring-test/src/test/java/org/springframework/test/web/servlet/samples/context


### Почитать

1. Unit тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#unit-testing
2. Интеграционное тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#integration-testing
3. Тестирование в Spring Boot https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html

### Задание

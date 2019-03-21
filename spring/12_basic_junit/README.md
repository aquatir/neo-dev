# 12_basic_junit

### Цель

Научиться тестировать Spring Boot приложения

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



Киллер фичей поддержки тестирования в Spring является тот факт, что ```ApplicationContext``` будет загружен 1 раз за весь запуск
задачи на тестирования (например через maven/gradle). Хотя у разработчика есть возможность повлиять на это поведение.



### Почитать

1. Unit тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#unit-testing
2. Интеграционное тестирование в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#integration-testing
3. Тестирование в Spring Boot https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html

### Задание

# 02_profile_specific

### Цель

Научится работать с профилями в Spring.

### Теория

Во время старта Spring / Spring boot приложения одним из важных компонентов, создаваемых в первую очередь, является интерфейс
```Environment```. В имплементации этого интерфейса будут содержаться вся конфигурационная информация о приложении. 

В ```Environment``` в частности можно задать профиль. Профиль представляет собой маркер, который можно
обработан при старте контекста или при работе приложения. Чтобы создать контекст с разными параметрами конфигруации.

Профиль можно использовать:
- Для обозначения файла (файлов) конфигурации, которые будут обработаны приложением (Для Spring boot). При старте Spring Boot
приложения, оно просматривается все файлы конфигурации с именем ```application-*PROFILE*.yml/.properties``` и начитывает
конфигурации из них всех. Более подробно про использование профилей для конфигурации можно почитать 
[здесь[1]](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files)
- Для создание разных бинов в зависимости от профиля.
- Для реализации различного поведения существующих методов/классов приложения в зависимости от профиля.

Есть разные подходы для работы с профилями. Одним из самых распространненых является создание профиля для каждой среды работы
приложения. Например:
- Локально приложение работает в профиле ```local```. Здесь прописывается коннет к локальной базе, все внешние адреса обозначены 
через localhost 
- Локальное тестирование приложения происходит в профиле ```test```. Здесь весь внешний мир представлен в виде моков
- Интеграционное тестирование происходит в профиле ```test-integration```. 
- На слой разработки приложение разворачивается с профилем ```dev```.
- И так далее.

Приложение может работать в нескольких профилях одновременно. Часто можно использовать комбинацию профилей.

#### Задание профиля

Существует несколько способов задавать профиль. Про все из них можно почитать [здесь[2]](https://www.baeldung.com/spring-profiles). 
Ниже представлены наиболее распространеннные:
- Передавать профиль в качестве параметров командной строки при старте приложения
- Экспортировать через переменные окружения в UNIX системах ```export spring_profiles_active=dev```
- Задавать профиль для тестов при помощи аннотации ```@ActiveProfiles("test")```
- Добавлять используемые профили в файлах конфигурации ```spring.profiles.include: integration_mock, running_tests``` 
(Более того - можно загрузить файл кофинуграции в зависимости от профиля, и в это файле конфигурации прописать другие профили!)

--- 

#### Использование профилей.

##### Для создания бинов в зависимости от профиля
```
@Component
@Profile("!test")
public class ProductionMyService implements MyService {}


@Component
@Profile("test")
public class MockMyService implements MyService {}

```

В данном случае будет подключен ```MockMyService``` для профиля ```test``` и ```ProductionMyService``` при отсутствии профиля тест.


##### Реализация поведения в зависимости от профиля

Для начала необходимо получить доступ к профилям внутри приложения. Самый простой способ - это использовать интерфейс 
Spring'а ```EnvironmentAware```. Данный интерфейс позволяет получить из любого бина доступ к объекту ```Environment```, а 
этот объект по мимо прочего также содержит все активные профили. Более подробно про Aware интерфейсы можно посмотреть у 
[здесь[3]](https://spring.io/blog/2016/11/02/spring-tips-spring-aware-beans)

```
@Service
public class MyProfileAwareService implements EnvironmentAware


    // Переменная, которая показывает, что приложение запущено с профилем running_tests
    private boolean isInTest;

    @Override
    public void setEnvironment(Environment environment) {
        var activeProfiles = Arrays.asList(environment.getActiveProfiles());
        this.isInTest = profilesAsList.contains("running_tests");
    }
    
    
    public void mySomeMethod() {
        if (isInTest) {
            return; // Не выполняем метод, если приложение в профиле running_tests
        }
        <...>
    }
```

### Почитать

1. Документация про конфигурацию Spring Boot приложения https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html#boot-features-external-config-application-property-files
2. Сборная солянка на тему "все о профилямх" https://www.baeldung.com/spring-profiles
3. https://spring.io/blog/2016/11/02/spring-tips-spring-aware-beans
4. Справка по профилям из официальной доки https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html

### Задание

Есть Spring Boot приложение с 1 интерфейсом-сервисом и 2 его реалиацями. Неоходимо, чтобы
1. ```ProductionMyService``` при вызове метода ```generateString``` всегда возвращал строку "NOT in mock"
2. ```MockMyService``` при вызове с профилем ```mock``` всегда возвращал строку "In mock"
3. ```MockMyService``` при вызове с профилем ```mock``` и с профилем ```test``` возвращал строку "In mock In test"

При этом:
1. Тест ```MockMyServiceTest``` должен запускаться только с профилем ```mock```
2. Тест ```MockMyServiceInTestModeTest``` должен запускатся с 2 профилями - ```mock``` и ```test``` 

(Подсказка: тесты можно насильно заставить запускаться в определенном профиле. См аннотацию ```@ActiveProfiles```)
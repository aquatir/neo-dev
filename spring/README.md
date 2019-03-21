# neo-dev
## Spring

### Неделя 1. Контекст

Задания:
- ```01_env_on_startup/``` 	-> Как работает ApplicationEvent. Выписать все переменные окружения / параметры среды, с которыми стартует Spring приложение
- ```02_profile_specific/``` 	-> Как заставить что-то работать по разному, в зависимости от активного профиля. ApplicationContextAware.
- ```03_rename_bean/``` 	-> Как при помощи BeanFactoryPostProcessor подменить BeanDefinition.
- ```04_aop_method/``` 		-> AOP в спринге. Как перехватить вызов метода и исполнить произвольный код.


### Неделя 2. Web + Security

Задания:
- ```05_api_version_resolver/``` 	-> Как создать 2 HTTP эндпоинта, который принимают запросы разных версий. 
- ```06_pass_filter_params/```		-> Как передать параметры GET запроса
- ```07_spring_security/```		-> Как сделать кастомный фильтр в Spring Security



### Неделя 3. Работа с БД, Spring Data

Задания:
- ```08_paging_sorting_query/```	-> Как работает пагинация и фильтрация в Spring Data
- ```09_life_without_spring_data/```	-> Как работать с Spring JDBC Template.
- ```10_entity_graphs/```		-> Как бороться с неоптимальными запросами в Spring Data при генерации запросов из имен методов в репозиториях
- ```11_speicification_api/```		-> Что можно делать при помощи Specification API.


### Неделя 4. Тестирование в Spring. Testcontainers.

Задания:
- ```12_basic_junit/```			-> Как работать с junit в контексте Spring.
- ```13_own_context_testcontainers/```	-> Как создать свой контекст для теста. Работа с testcontainers
- ```14_mocks/```			-> Как работают моки/спаи в mockito.

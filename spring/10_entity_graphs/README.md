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
и кучка других интерфейсов, наслдедующихся он него. Основными из них являются  ```JpaRepository```. 
Этот интерфейс предоставляет целую пачку запросов - запрос одного/нескольких элемента, paging/sorting и т.д. прямо из коробки.
 
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

Более подробно про репозитории можно почитить [здесь[3]](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories),
а [здесь[3]](https://github.com/spring-projects/spring-data-examples/tree/master/jpa) можно посмотреть примеры работы с JPA.

Но в чем же подлянка? В том, как работает ленивая инициализация объектов внутри транзакции

#### Маппинги и EntityGraph

Пусть у нас есть сущность ```Employee```:
```

```

Проблема Spring Data JPA заключается в том, что автогенерация запросов также являются главной убийцей производительности 
Spring Data JPA...

В чем суть? Во-первых, все еще есть [правила создания SQL запросов[1]](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation), 
по имени метода, ссылка на которые была выше.

---

А как решать эту проблему? Есть несколько путей

##### HQL

JPA говорит: у вас должен быть язык запросов JPQL. Hibernate имееют такой язык под названием HQL. [Вот его документация[4]](https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html)


##### EntityGraph


Есть еще 1 путь: **Выбросьте этот JPA и пишите на JDBC Template / JOOQ**.


### Почитать

1. Создание SQL запроса по тексту метода https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
2. Документация https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
3. Официальные примеры Spring Data JPA https://github.com/spring-projects/spring-data-examples/tree/master/jpa
4. Документация HQL https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html
 
- queryDSL http://www.querydsl.com/


### Задание

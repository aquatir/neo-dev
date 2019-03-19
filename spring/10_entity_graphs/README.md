# 10_entity_graphs

### Цель

Научиться делать эффективные запросы при помощи JPA. 

### Теория

Java persistence API - прекрасная абстракция для быстрой реализации взаимодействия с базой данных. К сожалению довольно 
часто быстрая != эффективная. Не смотря на то что мы уже выяснили, что для получения скорости при работе с БД лучше 
сразу использовать простые обертки, такие как Spring JDBC Template или JOOQ (платный или бесплатный, если работа с opensource БД),
имеет смысл рассказать об особенностях работы JPA, которые замедляют работу.

Активация работы с Spring JPA начинается с аннотации ```@EnableJpaRepositories``` (В Spring boot она ставится автоматически).
 Главным интрфейсом в Spring Data JPA является ```Repository``` и его подинтерфейсов. Основными из них являются 
 ```JpaRepository```. Этот интерфейс предоставляет целую пачку запросов - запрос одного/нескольких элемента, paging/sorting и т.д.
 
Киллер-фичей Spring Data JPA является автоматическая генерация запросов из имени метода. Такой подход позволяет не писать
SQL вообще! В Spring Data мы можем абсолютно легально написать:

```
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Stream<Employee> findAllAsStreamBy();
    Optional<Employee> findMaybeOneById(Long id);
}
```

И соответственно обрабатывать данные сразу в виде стрима или ```Optional``` (Note: ```Stream``` доступен только если взаимодействие 
с ним происходит внутри транзакции. Это требования связано с необходимостью закрытия стрима до флаша persistence context).

#### Маппинги и EntityGraph


Проблема Spring Data JPA заключается в том, что автогенерация запросов также являются главной убийцей производительности 
Spring Data JPA...


### Почитать

- Документация https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories
- Официальные примеры Spring Data JPA https://github.com/spring-projects/spring-data-examples/tree/master/jpa
- queryDSL http://www.querydsl.com/


### Задание

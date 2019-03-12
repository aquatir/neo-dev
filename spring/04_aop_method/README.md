# 04_spring_aop

### Цель

Понять, как работает AOP в спринг

### Теория


#### Общая концепция 
Аспектно-ориентированное программирование (Aspect Oriented Programming (AOP)) - подход, основанный на выделении 
"сквозной" функциональности приложения в отдельные блоки (такая функциональность называется ```cross-cutting concern```) 
и реализации этой функциональности в выделенных компонентах. Пример - работа с транзакциями в реляционной базе данных. 
Всякий раз перед началом взаимодействиуя с БД необходимо создавать транзакцию, а после взаимодействие - закрывать её 
(commit/rollback). При этом, такие действия необходимо делать вне зависимости от конкретного действия, выполняемого над 
базой данных. Значит открытие/закрытие транзакций можно вынести в отдельный блок и реализовать один раз. 
Подобные задачи называются ```cross-cutting concern```. Классическими примерами таких задач являются:
- Работа с транзакциями
- Логирование
- Сбор метрок

Первой (но на сегодняшний день - не единственной) реализацией концепции AOP под Java стал 
[AspectJ[1]](https://www.eclipse.org/aspectj/doc/released/progguide/starting-aspectj.html). 

Важно понимать, что Spring AOP не ставит своей целью реализовать полностью все концепции аспектно-ориентированного программирование. 
Его задача - дать возможность использовать AOP при работе с бинами, управляемыми через контекст. ```AspectJ``` же является 
решением для *любой* проблемы, связанной с ```cross-cutting concerns```.

Тем неменее, различными хаками можно заставить Spring использовать AspectJ на полную. 
Вот [тут[2]](https://habr.com/ru/post/347752/) например описано, как этого можно добиться (Повторять такое на проде конечно 
же не стоит. Разве что вы точно не уверены в том, что делаете).


##### Терминология AOP и API

Подробно об AOP в Spring можно почитать [здесь[3]](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop)

Работа с AOP основана на нескольких важных терминах:
- *Aspect* - суть cross-cutting concert. Некоторая функциональность, работу которой нужно модуляризировать.
- *Join point* - Конкретное место, в котором происходит действие аспекта. В Spring AOP JP - это всегда вызов метода.
- *Advice* - Действия, которые необходимо выполнить в JP. Advice может быть выполнен ДО, ПОСЛЕ или ВОКРУГ вызова JP
- *Pointcut* - Предикат, соответствующий JP. Advice матчится по этому предикату, чтобы определить JP, для которого он будет выполнять действия
- *Weaving* -  Процесс "скрещивания" существующего кода с AOP функциональность. В случае со Spring AOP происходит в рантайме.

В Spring работа с AOP выглядит следующим образом:

Сначала определяем класс, помеченный аннотацией ```@Aspect```. В этом классе будет определен набор действия для работы
с конкретным cross-cutting concert
```
@Aspect
public class Logger {
    
}
``` 

Далее следует определить Pointcut'ы и задать Advice'ы. Эти 2 действия можно объеденить, как в примере:
```
@Aspect
public class Logger {
    
    @Pointcut("execution(* ru.neoflex.dev.spring.save(..))") // Здесь определеяется Pointcut - предикат
    private void saveMethodCalled() {}  
    
    @Before("saveMethodCalled()") // Можно использовать определенный ранее Pointcut
    public void logChangesBefore(JoinPoint joinPoint) {
        <... Your code executed BEFORE save ...>
    }
    
    @After("execution(* ru.neoflex.dev.spring.save(..))") // А можно определить Pointcut прямо внутри одного из advice'ов
    public void logChangesAfter(JoinPoint joinPoint) {
        <... Your code executed AFTER save ...>
    }
}
```

Затем следует реализовать метод перехватчика.

Следует помнить, что Spring AOP не поддерживает все виды перехватов, которые поддерживает AspectJ (Напирмер, cflow использовать
в спринге нельзя). Более подробно можно почитать [здесь[4]](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop-pointcuts-designators)


Отдельно стоит сказать про ```Around``` перехватчик. При работе с around перехватчиком где-то внутри кода аспекта должен
возвращаться результат работы метода при чем тип возвращаемого значения должен соответствовать тому типу, который бы вернулся из
метода, если бы перехватчика не было. В качестве примера, перехватчик который перехватывает вызовы методов помеченных
аннотацией ```ChangesAudited``` и запускающий ```Runnable```, логирующий перехваченный объект:

```
@Around("@annotation(ChangesAudited)")
public Object logChangedObjects(ProceedingJoinPoint joinPoint) throws Throwable {

    /* Get current user BEFORE execution of method because SecurityContext does not get propagated to thread pool executor */
    var currentEmp = Optional.ofNullable(employeeAggregateService.getCurrentLoggedInEmployee());
        
    var currentUserUsername = currentEmp.map(Employee::getUsername)
        .orElse("");

    var result = joinPoint.proceed();

    executor.submit(new AuditionRunnable(result, currentUserUsername));
    return result;
}
```

#### Где AOP используется в Spring из коробки

В случае со Spring возможности AOP применяются при работе с транзакциями и аннотацией ```@Transactional```. 
Аспект, отвечающий за обработку транзакций спринга называется ```AnnotationTransactionAspect```.
Аспект, отвечающий за обработку JTA транзакций называется ```JtaAnnotationTransactionAspect```. Помним, что аннотаций 
```@Transactional``` вообще говоря в Spring 2 штуки - она из самого спринга, вторая из JTA.



Note 1: ```@Transactional``` и любые другие аспекты Spring поумолчанию НЕ РАБОТАЮ, если они ставятся над private / package-private или protected 
методами. Также аннотация игнорируется, если из одного бина вызвать метод того же самого бина, помеченного аннотацией.

Note 2: Поумолчанию аннотации над интерфейсами не наследуется, поэтому рекомендуется ставить ```@Transactional``` над 
конкретными имплементациями (Хотя аннотации над интефрейсами и будут работать с JDK Dynamic Proxy, они не будут работать с CGLIB)

Note 3: Ставить ```@Transactional``` над методом, над которым стоит ```@PostConstruct```, тоже довольно плохая идея. Причина
заключается в том, что создание проксей и ```PostConstruct``` происходит одновременно. Не делайте так :)

Note 4: Работу ```@Transactional``` можно завязать на aspectJ напрямую, изменив параметр ```mode```. Это стоит делать только,
если вы реально понимаете, что делаете. Переход на aspectJ позволяет решит проблем из Note 1.

Более подробно о ```@Transactional``` мы еще поговорим в одном из следующих заданий. Пока же можно почитать
 официальную доку [здесь[5]](https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction-declarative-annotations)

---

Бонус: Про различия AsceptJ и Spring AOP можно почитать [здесь[6]](https://www.baeldung.com/spring-aop-vs-aspectj)

### Почитать

1. AspectJ документация https://www.eclipse.org/aspectj/doc/released/progguide/starting-aspectj.html
2. Как подружить Spring и AspectJ так, что Spring перестанет работать :) https://habr.com/ru/post/347752/
3. AOP в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop
4. Виды понткатов https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#aop-pointcuts-designators
5. Как работает ```@Transactional``` в спринге https://docs.spring.io/spring/docs/current/spring-framework-reference/data-access.html#transaction-declarative-annotations
6. Разница между Spring AOP и AspectJ  https://www.baeldung.com/spring-aop-vs-aspectj

### Задание

Есть сервис ```MyService```. В нем есть единственный метод ```printStuff```. Есть сервис ```CallsIncrementer```, который 
имеет 2 счетчика - один для подсчета вызовом ДО, второй для подсчета вызовом ПОСЛЕ. Есть тест, который запускает метод 
```printStuff```, а затем проверяет, положились ли в CallIncrementer две единички - 1 для запуска "ДО", вторая для 
запуска "ПОСЛЕ".

Необходимо написать аспект, который бы перехватывал вызов ```printStuff``` ДО исполнения и ПОСЛЕ исполнения. Каждый
перехват должен увеличивать на единичку соответствующий счетчик в ```CallsIncrementer```.

Для более веселой задачи (и более часто встречающейся в реальном мире) - рекомендуется перехват делать через аннотацию. 
Такую аннотацию можно будет добавить над методом ```printStuff``` и перехватить в вашем перехватчике. Другое изменение
класса ```MyService``` и ```MyAspectTest``` не допускается.

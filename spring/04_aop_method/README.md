# 04_spring_aop

### Цель

Понять, как работает AOP в спринг

### Теория

Aspect Oriented Programming (AOP) - подход 


```cross-cutting concerns```

Важно понимать, что Spring AOP не ставит своей целью реализовать полностью все концепции аспектно-ориентированного программирование. 
Его задача - дать возможность использовать AOP при работе с бинами, управляемыми через контекст. ```AspectJ``` же является 
решением для *любой* проблемы, связанной с ```cross-cutting concerns```.

Тем неменее, различными хаками можно заставить Spring использовать AspectJ на полную. 
Вот [тут[99]](https://habr.com/ru/post/347752/) например описано, как этого можно добиться (Повторять такое конечно же не стоит)

---



### Почитать

1. AspectJ документация https://www.eclipse.org/aspectj/doc/released/progguide/starting-aspectj.html
2. Разница между Spring AOP и AspectJ https://www.baeldung.com/spring-aop-vs-aspectj
3. Как подружить Spring и AspectJ так, что Spring перестанет работать :) https://habr.com/ru/post/347752/
    
### Задание

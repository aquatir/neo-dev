package ru.neoflex.dev.spring.aop_method.stuff;

import org.springframework.stereotype.Service;

@Service
public class MyService {

    public MyService() {};

    public void printStuff() {
        System.out.println("Printing...");
    }
}

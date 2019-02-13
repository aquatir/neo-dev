package ru.neoflex.dev.spring.aop_method.stuff;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MyAspectTest {

    @Autowired private CallsIncrementer callsIncrementer;
    @Autowired private MyService myService;


    @Test
    public void beforeAndAfterCalledCorrectNumberOfTimes() {
        myService.printStuff();

        Assert.assertEquals(1, callsIncrementer.getCallsAfterCount());
        Assert.assertEquals(1, callsIncrementer.getCallsBeforeCount());
    }
}

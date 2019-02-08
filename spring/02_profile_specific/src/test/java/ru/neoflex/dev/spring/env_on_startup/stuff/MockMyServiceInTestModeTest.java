package ru.neoflex.dev.spring.env_on_startup.stuff;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MockMyServiceInTestModeTest {

    @Autowired
    private MyService myService;

    @Test
    public void testMyService() {
        Assert.assertEquals("In mock In test", myService.generateString());
    }
}

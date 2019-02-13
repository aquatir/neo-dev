package ru.neoflex.dev.spring.rename_bean.stuff;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ShapeShifterTest {

    @Autowired ShapeShifter shapeShifter;

    @Test
    public void testShapeShifterProperty() {
        Assert.assertEquals(10, shapeShifter.getMyProp());
    }
}

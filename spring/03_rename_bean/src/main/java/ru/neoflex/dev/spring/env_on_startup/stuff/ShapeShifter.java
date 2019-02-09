package ru.neoflex.dev.spring.env_on_startup.stuff;

import org.springframework.beans.factory.annotation.Value;

public class ShapeShifter {

    @Value("${myProp}")
    private int myProp;

    public int getMyProp() {
        return myProp;
    }

    public void setMyProp(int myProp) {
        this.myProp = myProp;
    }
}

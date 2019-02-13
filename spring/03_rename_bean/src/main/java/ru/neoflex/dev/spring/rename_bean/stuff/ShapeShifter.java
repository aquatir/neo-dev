package ru.neoflex.dev.spring.rename_bean.stuff;

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

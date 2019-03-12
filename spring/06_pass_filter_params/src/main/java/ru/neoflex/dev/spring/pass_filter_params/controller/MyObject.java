package ru.neoflex.dev.spring.pass_filter_params.controller;

import java.time.LocalDateTime;

public class MyObject {

    private LocalDateTime date;

    private String otherValue;

    private String veryOtherValue;

    public MyObject() {}

    public LocalDateTime getDate() {
        return date;
    }

    public String getOtherValue() {
        return otherValue;
    }

    public String getVeryOtherValue() {
        return veryOtherValue;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setOtherValue(String otherValue) {
        this.otherValue = otherValue;
    }

    public void setVeryOtherValue(String veryOtherValue) {
        this.veryOtherValue = veryOtherValue;
    }
}


package ru.neoflex.dev.spring.pass_filter_params.controller;

public class FilterParams {
    private String name;
    private int age;

    public FilterParams() {}

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

package ru.neoflex.dev.spring.env_on_startup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @PostMapping("/testcall1")
    public String testCallOne(MyObject request) {
        return request.getDate() + request.getOtherValue() + request.getVeryOtherValue();
    }

    @GetMapping("/testcall2/{path}")
    public String testCallTwo(int path, FilterParams filter) {
        return path + filter.getAge() + filter.getName();
    }
}

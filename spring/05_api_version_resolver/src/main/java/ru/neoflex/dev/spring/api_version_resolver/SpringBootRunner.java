package ru.neoflex.dev.spring.api_version_resolver;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@SpringBootApplication
public class SpringBootRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootRunner.class, args);
    }
}

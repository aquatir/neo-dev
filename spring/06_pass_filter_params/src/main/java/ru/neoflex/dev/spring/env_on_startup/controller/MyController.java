package ru.neoflex.dev.spring.env_on_startup.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class MyController {

    @PostMapping("/")
    public MyObject call(@RequestBody MyRequest request) {
        return null;
    }

    @GetMapping("/object/{id}/blalba/{something}")
    public String callz(@PathVariable int id, @PathVariable(name = "something") int id2) {
        System.out.println(id);
        System.out.println(id2);
        return "String";
    }


    private class MyRequest {
        private Long id;
        private Optional<String> name;
        private LocalDate date;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private class MyObject {

        private Long id;
        private String name;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime date;

        @JsonProperty("compelte_different_name")
        private String otherValue;
    }
}

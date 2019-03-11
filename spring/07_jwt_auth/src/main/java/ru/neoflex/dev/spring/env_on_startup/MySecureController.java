package ru.neoflex.dev.spring.env_on_startup;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class MySecureController {

    @GetMapping("/secure")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String hello(@AuthenticationPrincipal Principal principal) {
        return "Current principal: " + principal.getName();
    }
}

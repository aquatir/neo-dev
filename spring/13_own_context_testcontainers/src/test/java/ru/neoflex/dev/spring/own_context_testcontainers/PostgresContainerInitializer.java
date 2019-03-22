package ru.neoflex.dev.spring.own_context_testcontainers;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    @Override
    public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {

    }
}

package ru.neoflex.dev.spring.own_context_testcontainers;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

public class PostgresContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static PostgreSQLContainer postgres =
            (PostgreSQLContainer) new PostgreSQLContainer("postgres:10.5")
                    .withDatabaseName("dev")
                    .withUsername("bai")
                    .withPassword("test-password")
                    .withStartupTimeout(Duration.ofSeconds(600))
                    .withExposedPorts(5432);

    public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url=" + postgres.getJdbcUrl(),
                "spring.datasource.username=" + postgres.getUsername(),
                "spring.datasource.password=" + postgres.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}

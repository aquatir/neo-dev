package ru.neoflex.dev.spring.env_on_startup.stuff;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
    @Bean
    public ShapeShifter shapeShifter() {

        return new ShapeShifter();
    }
}

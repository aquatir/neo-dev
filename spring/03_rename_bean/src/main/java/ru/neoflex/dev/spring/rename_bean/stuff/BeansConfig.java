package ru.neoflex.dev.spring.rename_bean.stuff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
    @Bean
    public ShapeShifter shapeShifter() {

        return new ShapeShifter();
    }
}

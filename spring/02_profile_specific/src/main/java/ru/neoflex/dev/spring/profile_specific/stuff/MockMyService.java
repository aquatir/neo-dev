package ru.neoflex.dev.spring.profile_specific.stuff;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mock")
public class MockMyService implements MyService {

    @Override
    public String generateString() {
        return "In mock";
    }
}

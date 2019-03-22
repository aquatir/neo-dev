package ru.neoflex.dev.spring.mocks.controller;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@SqlGroup({
        @Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class EmployeeControllerTest {

}

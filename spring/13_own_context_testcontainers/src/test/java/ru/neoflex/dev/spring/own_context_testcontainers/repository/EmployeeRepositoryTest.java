package ru.neoflex.dev.spring.own_context_testcontainers.repository;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.neoflex.dev.spring.own_context_testcontainers.PostgresContainerInitializer;

import static org.junit.Assert.*;

@SpringBootTest
@ContextConfiguration(initializers = {PostgresContainerInitializer.class})
@RunWith(SpringRunner.class)

@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired private EmployeeRepository employeeRepository;

    @Test
    public void test_findAll() {
        var emps = this.employeeRepository.findAll();
        assertEquals(4, emps.size());
    }
}

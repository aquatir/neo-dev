package ru.neoflex.dev.spring.env_on_startup.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void test_FindOne() {
        var maybeEmp = this.employeeRepository.findById(1L);
        assertTrue(maybeEmp.isPresent());
    }

    @Test
    public void test_findAll() {
        var emps = this.employeeRepository.findAll();
        assertEquals(4, emps.size());
    }


}

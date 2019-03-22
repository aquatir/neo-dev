package ru.neoflex.dev.spring.mocks.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@SqlGroup({
        @Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class EmployeeServiceTest {

    @MockBean
    private EmployeeService employeeService;

    @Before
    public void setUp() {

    }

    @Test
    public void test_findById_One() {
        var emp = this.employeeService.findById(1L);
        assertEquals("TEST-ONE", emp.get().getName());
    }

    @Test
    public void test_findById_Two() {
        var emp = this.employeeService.findById(2L);
        assertEquals("TEST-TWO", emp.get().getName());
    }

    @Test
    public void test_findById_Random() {
        var rnd = new Random();

        var emp = this.employeeService.findById(rnd.nextInt(10000) + 2L);
        assertEquals("TEST-ANT", emp.get().getName());
    }

}

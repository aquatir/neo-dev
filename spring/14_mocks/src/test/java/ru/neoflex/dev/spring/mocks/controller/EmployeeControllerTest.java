package ru.neoflex.dev.spring.mocks.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import ru.neoflex.dev.spring.mocks.entity.Employee;
import ru.neoflex.dev.spring.mocks.service.EmployeeService;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@SqlGroup({
        @Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class EmployeeControllerTest {

    @SpyBean
    private EmployeeService employeeService;

    @Before
    public void setUp() {
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var emp = this.employeeService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }

    @Test
    public void test_findAll() {
        var emps = this.employeeService.findAll();
        assertEquals(4, emps.size());
    }
}

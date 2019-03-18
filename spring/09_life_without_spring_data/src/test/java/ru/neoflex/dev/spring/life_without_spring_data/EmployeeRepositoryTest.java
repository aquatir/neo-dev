package ru.neoflex.dev.spring.life_without_spring_data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.neoflex.dev.spring.life_without_spring_data.entity.Department;
import ru.neoflex.dev.spring.life_without_spring_data.entity.Employee;
import ru.neoflex.dev.spring.life_without_spring_data.repository.EmployeeRepository;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired private EmployeeRepository employeeRepository;

    @Test
    public void test_FindAll_ExpectSuccess() {
        var result = employeeRepository.findAll();
        Assert.assertEquals(5, result.size());
    }

    @Test
    public void test_FindOne_Lazy_ExpectSuccess() {
        var result = employeeRepository.findOneLazyById(2L);
        Assert.assertEquals("Mark", result.getName());
    }

    @Test
    public void test_InsertNew_ExpectSuccess() {
        var newEmp = Employee.builder()
                .name("TESTER")
                .build();

        var result = employeeRepository.insertNew(newEmp);
        Assert.assertEquals(6L, (long) result);
    }

    @Test
    public void test_InsertNewAndGet_ExpectSuccess() {
        var newEmp = Employee.builder()
                .name("TESTER")
                .build();
        var result = employeeRepository.insertNewAndGet(newEmp);
        Assert.assertEquals("TESTER", result.getName());
    }


    @Test
    public void test_FindOne_Eager_ExpectSuccess() {
        var result = employeeRepository.findOneEagerlyById(4L);
        Assert.assertEquals("Susan", result.getName());
        Assert.assertEquals("dep-dep", result.getDepartment().map(Department::getName).orElseThrow());
    }

}

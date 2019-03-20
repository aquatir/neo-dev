package ru.neoflex.dev.spring.specification_api.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeServiceTest {
    @Autowired
    private EmployeeService employeeService;

    @Test
    public void test_getAllOlderThen20() {
        var olderThen20 = employeeService.allOlderThen20();

        Assert.assertTrue(olderThen20.stream().allMatch(emp -> emp.getAge() > 20));
        Assert.assertEquals(2, olderThen20.size());
    }

    @Test
    public void test_getAllInFrir() {
        var inFrir = employeeService.allInFRIR();

        Assert.assertTrue(inFrir.stream().allMatch(emp -> emp.getDepartment().getName().equals("FRIR")));
        Assert.assertEquals(2, inFrir.size());
    }

    @Test
    public void test_getAllInFrirAndOlderThen20() {
        var olderThen20InFrir = employeeService.allOlderThen20InFRIR();

        Assert.assertTrue(olderThen20InFrir.stream().allMatch(emp -> emp.getDepartment().getName().equals("FRIR")));
        Assert.assertTrue(olderThen20InFrir.stream().allMatch(emp -> emp.getAge() > 20));
        Assert.assertEquals(1, olderThen20InFrir.size());
    }
}

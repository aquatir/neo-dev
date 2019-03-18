package ru.neoflex.dev.spring.life_without_spring_data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.neoflex.dev.spring.life_without_spring_data.repository.DepartmentRepository;


@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    public void test_FindAll_ExpectSuccess() {
        var result = departmentRepository.findAll();
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void test_FindOne_Lazy_ExpectSuccess() {
        var result = departmentRepository.findOneLazyById(1L);
        Assert.assertEquals("dep-dep", result.getName());
    }

    @Test
    public void test_FindEagerly_Except_Success() {
        var result = departmentRepository.findOneEagerlyById(1L);
        Assert.assertEquals(2, result.getEmployees().size());
        var susan = result.getEmployees().stream().filter(oneEmp -> oneEmp.getName().equals("Susan")).findFirst();
        var vlad = result.getEmployees().stream().filter(oneEmp -> oneEmp.getName().equals("Vlad")).findFirst();

        Assert.assertTrue(susan.isPresent());
        Assert.assertTrue(vlad.isPresent());

    }
}

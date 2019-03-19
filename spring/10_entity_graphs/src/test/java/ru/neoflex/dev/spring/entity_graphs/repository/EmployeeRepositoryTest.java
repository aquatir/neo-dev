package ru.neoflex.dev.spring.entity_graphs.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeRepositoryTest {

    @Autowired private EmployeeRepository employeeRepository;

    @Test
    @Transactional
    public void test_StreamGetAll_ExpectSuccess() {
        var numOfEntries = this.employeeRepository.findAllAsStreamBy().count();
        Assert.assertEquals(3, numOfEntries);
    }

    @Test
    public void test_MaybeFindOne_ExceptSuccess() {
        var maybeEmployee = this.employeeRepository.findMaybeOneById(4L);
        Assert.assertFalse(maybeEmployee.isPresent());
    }


}

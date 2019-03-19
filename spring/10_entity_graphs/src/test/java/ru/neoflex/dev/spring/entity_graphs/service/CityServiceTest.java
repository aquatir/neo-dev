package ru.neoflex.dev.spring.entity_graphs.service;

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
public class CityServiceTest {
    @Autowired private CityService cityService;

    @Test
    public void test_PrintCities_ExceptSuccess() {
        var emps = this.cityService.printAllEmfsInAllCities();
        System.out.println(emps);
        Assert.assertNotNull(emps);
    }

}

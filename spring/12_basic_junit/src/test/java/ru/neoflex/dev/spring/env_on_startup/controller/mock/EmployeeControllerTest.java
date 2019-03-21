package ru.neoflex.dev.spring.env_on_startup.controller.mock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.neoflex.dev.spring.env_on_startup.controller.EmployeeController;
import ru.neoflex.dev.spring.env_on_startup.entity.Employee;
import ru.neoflex.dev.spring.env_on_startup.service.EmployeeService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService employeeService;

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Test
    public void test_AsMock_FindAll() throws Exception {
        given(this.employeeService.findAll())
                .willReturn(List.of(
                        Employee.builder()
                                .id(1L)
                                .name("test1")
                                .age(1L)
                                .build(),
                        Employee.builder()
                                .id(2L)
                                .name("test2")
                                .age(2L)
                                .build()));


        this.mvc.perform(get("/employee").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":1,\"name\":\"test1\",\"age\":1,\"department\":null},{\"id\":2,\"name\":\"test2\",\"age\":2,\"department\":null}]"));

        assertThat(capture.toString(), containsString("TEST-TEST"));
    }

    @Test
    public void test_AsMock_FindOneById() throws Exception {
        given(this.employeeService.findById(anyLong()))
                .willReturn(
                        Optional.of(Employee.builder()
                                .id(1L)
                                .name("test1")
                                .age(1L)
                                .build())
                        );

        this.mvc.perform(get("/employee/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"test1\",\"age\":1,\"department\":null}"));
    }
}

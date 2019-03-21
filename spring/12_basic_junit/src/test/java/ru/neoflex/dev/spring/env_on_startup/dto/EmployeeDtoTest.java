package ru.neoflex.dev.spring.env_on_startup.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JsonTest
public class EmployeeDtoTest {

    @Autowired
    private JacksonTester<EmployeeDto> json;

    @Test
    public void testSerialize() throws Exception {
        var employeeDto = EmployeeDto.builder()
                .id(1L)
                .name("testEmp")
                .age(5L)
                .department(
                        DepartmentDto.builder()
                                .id(1L)
                                .name("testDep")
                                .build()
                )
                .build();

        // Assert against a `.json` file in the same package as the test
        assertThat(this.json.write(employeeDto)).isEqualToJson("kek");
        // Or use JSON path based assertions
//        assertThat(this.json.write(details)).hasJsonPathStringValue("@.make");
//        assertThat(this.json.write(details)).extractingJsonPathStringValue("@.make")
//                .isEqualTo("Honda");
    }
}

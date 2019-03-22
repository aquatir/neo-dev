package ru.neoflex.dev.spring.env_on_startup.dto;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JsonTest
public class EmployeeDtoTest {

    @Autowired
    private JacksonTester<EmployeeDto> json;

    private EmployeeDto employeeDto = EmployeeDto.builder()
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


    @Test
    public void testSerialize() throws Exception {
        assertThat(this.json.write(this.employeeDto)).isEqualToJson("/jsons/employeeDto.json");
    }

    @Test
    public void testDeserialize() throws Exception {
        var jsonText = "{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"testEmp\",\n" +
                "  \"age\": 5,\n" +
                "  \"department\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"testDep\"\n" +
                "  }" +
                "}";

        var testEmp = this.json.parseObject(jsonText);
        assertThat(testEmp.getAge()).isEqualTo(this.employeeDto.getAge());
        assertThat(testEmp.getName()).isEqualTo(this.employeeDto.getName());
        assertThat(testEmp.getDepartment().getName()).isEqualTo(this.employeeDto.getDepartment().getName());
    }
}

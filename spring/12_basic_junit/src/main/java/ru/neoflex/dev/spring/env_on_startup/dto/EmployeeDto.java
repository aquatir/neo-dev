package ru.neoflex.dev.spring.env_on_startup.dto;

public class EmployeeDto {

    private Long id;
    private String name;
    private Long age;
    private DepartmentDto department;

    public EmployeeDto() {
    }


    public static EmployeeDtoBuilder builder() {
        return new EmployeeDtoBuilder();
    }

    public static final class EmployeeDtoBuilder {
        private Long id;
        private String name;
        private Long age;
        private DepartmentDto department;

        private EmployeeDtoBuilder() {
        }

        public EmployeeDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EmployeeDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EmployeeDtoBuilder age(Long age) {
            this.age = age;
            return this;
        }

        public EmployeeDtoBuilder department(DepartmentDto department) {
            this.department = department;
            return this;
        }

        public EmployeeDto build() {
            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.name = this.name;
            employeeDto.age = this.age;
            employeeDto.department = this.department;
            employeeDto.id = this.id;
            return employeeDto;
        }
    }
}

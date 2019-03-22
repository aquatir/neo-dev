package ru.neoflex.dev.spring.mocks.dto;


import ru.neoflex.dev.spring.mocks.entity.Employee;

import java.util.Optional;

public class EmployeeDto {

    private Long id;
    private String name;
    private Long age;
    private DepartmentDto department;

    public EmployeeDto() {
    }

    public static EmployeeDto ofEmployee(Employee emp) {
        return EmployeeDto.builder()
                .id(emp.getId())
                .age(emp.getAge())
                .name(emp.getName())
                .department(Optional.ofNullable(emp.getDepartment())
                        .map(actualDep -> DepartmentDto.ofDepartment(emp.getDepartment()))
                        .orElse(null)
                ).build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public DepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDto department) {
        this.department = department;
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

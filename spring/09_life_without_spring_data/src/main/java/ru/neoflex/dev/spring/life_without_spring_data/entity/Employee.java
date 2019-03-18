package ru.neoflex.dev.spring.life_without_spring_data.entity;

import org.springframework.jdbc.core.RowMapper;

import java.util.Optional;


public class Employee {
    private Long id;
    private String name;
    private Optional<Department> department;

    public Employee() {
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

    public Optional<Department> getDepartment() {
        return department;
    }

    public void setDepartment(Optional<Department> department) {
        this.department = department;
    }

    public static RowMapper<Employee> ROW_MAPPER_LAZY = (rs, rowNum) -> Employee.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .department(Department
                    .builder()
                    .id(rs.getLong("department_id"))
                    .build())
            .build();

    public static RowMapper<Employee> ROW_MAPPER_WITH_DEPARTMENT = (rs, rowNum) -> Employee.builder()
            .id(rs.getLong("EMPLOYEE.ID"))
            .name(rs.getString("EMPLOYEE.NAME"))
            .department(Department
                    .builder()
                    .id(rs.getLong("DEPARTMENT.ID"))
                    .name(rs.getString("DEPARTMENT.NAME"))
                    .build())
            .build();

    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    public static final class EmployeeBuilder {
        private Long id;
        private String name;
        private Department department;

        private EmployeeBuilder() {
        }

        public EmployeeBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public EmployeeBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EmployeeBuilder department(Department department) {
            this.department = department;
            return this;
        }

        public Employee build() {
            Employee employee = new Employee();
            employee.setId(this.id);
            employee.setName(this.name);
            employee.setDepartment(Optional.ofNullable(this.department));
            return employee;
        }
    }


}

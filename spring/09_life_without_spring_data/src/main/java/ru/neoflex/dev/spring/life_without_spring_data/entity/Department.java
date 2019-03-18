package ru.neoflex.dev.spring.life_without_spring_data.entity;

import org.springframework.jdbc.core.RowMapper;

import java.util.List;

public class Department {
    private Long id;
    private String name;
    private List<Employee> employees;


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

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Department(Long id, String name, List<Employee> employees) {
        this.id = id;
        this.name = name;
        this.employees = employees;
    }

    public static RowMapper<Department> ROW_MAPPER_NO_EMP = (rs, rowNum) -> new Department(
            rs.getLong("id"),
            rs.getString("name"),
            null
    );

    public static DepartmentBuilder builder() {
        return new DepartmentBuilder();
    }

    public static final class DepartmentBuilder {
        private Long id;
        private String name;
        private List<Employee> employees;

        private DepartmentBuilder() {
        }

        public DepartmentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DepartmentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DepartmentBuilder employees(List<Employee> employees) {
            this.employees = employees;
            return this;
        }

        public Department build() {
            return new Department(id, name, employees);
        }
    }



}

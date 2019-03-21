package ru.neoflex.dev.spring.env_on_startup.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @GeneratedValue()
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "AGE", nullable = false)
    private Long age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID", nullable = true)
    private Department department;



    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getAge() {
        return age;
    }

    public Department getDepartment() {
        return department;
    }

    public static EmployeeBuilder builder() {
        return new EmployeeBuilder();
    }

    public static final class EmployeeBuilder {
        private Long id;
        private String name;
        private Long age;
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

        public EmployeeBuilder age(Long age) {
            this.age = age;
            return this;
        }

        public EmployeeBuilder department(Department department) {
            this.department = department;
            return this;
        }

        public Employee build() {
            Employee employee = new Employee();
            employee.id = this.id;
            employee.name = this.name;
            employee.department = this.department;
            employee.age = this.age;
            return employee;
        }
    }
}
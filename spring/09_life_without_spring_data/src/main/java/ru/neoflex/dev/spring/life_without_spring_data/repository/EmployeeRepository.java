package ru.neoflex.dev.spring.life_without_spring_data.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.neoflex.dev.spring.life_without_spring_data.dao.DepartmentDao;
import ru.neoflex.dev.spring.life_without_spring_data.dao.EmployeeDao;
import ru.neoflex.dev.spring.life_without_spring_data.entity.Employee;

import java.util.List;

/** Provides high-level access to Employee table (employee and all linked tables*/
@Component
public class EmployeeRepository {
    private final EmployeeDao employeeDao;
    private final DepartmentDao departmentDao;
    private final JdbcTemplate jdbcTemplate;

    /* This could be simplified into 2 queries */
    private final String FIND_ONE_BY_ID_WITH_DEPARTMENT = "SELECT * FROM EMPLOYEE as EMPLOYEE " +
            "JOIN DEPARTMENT as DEPARTMENT " +
            "ON (EMPLOYEE.DEPARTMENT_ID = DEPARTMENT.ID)" +
            " WHERE EMPLOYEE.ID = ?";

    public EmployeeRepository(EmployeeDao employeeDao, DepartmentDao departmentDao, JdbcTemplate jdbcTemplate) {
        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
        this.jdbcTemplate = jdbcTemplate;
    }


    public Long insertNew(Employee employee) {
        return this.employeeDao.insertNew(employee);
    }

    public Employee insertNewAndGet(Employee employee) {
        return this.employeeDao.insertNewAndGet(employee);
    }

    public List<Employee> findAll() {
        return this.employeeDao.findAll();
    }

    public Employee findOneLazyById(Long id) {
        return this.employeeDao.findOneLazyById(id);
    }

    public Employee findOneEageryById(Long id) {
        return jdbcTemplate.queryForObject(FIND_ONE_BY_ID_WITH_DEPARTMENT, new Object[]{id}, Employee.ROW_MAPPER_WITH_DEPARTMENT);
    }



}

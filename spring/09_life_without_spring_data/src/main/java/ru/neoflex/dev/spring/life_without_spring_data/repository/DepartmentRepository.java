package ru.neoflex.dev.spring.life_without_spring_data.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.dev.spring.life_without_spring_data.dao.DepartmentDao;
import ru.neoflex.dev.spring.life_without_spring_data.dao.EmployeeDao;
import ru.neoflex.dev.spring.life_without_spring_data.entity.Department;

import java.util.List;

@Component
public class DepartmentRepository {
    private final EmployeeDao employeeDao;
    private final DepartmentDao departmentDao;
    private final JdbcTemplate jdbcTemplate;

    public DepartmentRepository(EmployeeDao employeeDao, DepartmentDao departmentDao, JdbcTemplate jdbcTemplate) {
        this.employeeDao = employeeDao;
        this.departmentDao = departmentDao;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Department> findAll() {
        return this.departmentDao.findAll();
    }

    public Department findOneLazyById(Long id) {
        return this.departmentDao.findOneLazyById(id);
    }

    /** Find departments and associated employees */
    @Transactional(readOnly = true)
    public Department findOneEagerlyById(Long id) {
        var dep = this.findOneLazyById(id);
        var emps = this.employeeDao.findAllByDepartmentId(dep.getId());
        dep.setEmployees(emps);
        return dep;
    }
}

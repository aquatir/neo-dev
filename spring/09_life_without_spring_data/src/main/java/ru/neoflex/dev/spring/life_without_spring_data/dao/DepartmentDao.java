package ru.neoflex.dev.spring.life_without_spring_data.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.neoflex.dev.spring.life_without_spring_data.entity.Department;

import javax.sql.DataSource;
import java.util.List;

/**
 * Basic requests to Department table ONLY
 */
@Component
public class DepartmentDao {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public static final String FIND_ALL = "SELECT * FROM DEPARTMENT";

    public DepartmentDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public List<Department> findAll() {
        return jdbcTemplate.query(FIND_ALL, Department.ROW_MAPPER_NO_EMP);
    }

    public Department findOneLazyById(Long id) {
        return null;
    }
}

package ru.neoflex.dev.spring.life_without_spring_data.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.neoflex.dev.spring.life_without_spring_data.SqlParametersMapBuilder;
import ru.neoflex.dev.spring.life_without_spring_data.entity.Department;
import ru.neoflex.dev.spring.life_without_spring_data.entity.Employee;

import javax.sql.DataSource;
import java.util.List;

@Component
public class EmployeeDao {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public static String FIND_ALL = "SELECT * FROM EMPLOYEE";
    public static String FIND_ALL_BY_DEPARTMENT_ID = "SELECT * FROM EMPLOYEE WHERE DEPARTMENT_ID = ?";
    public static String FIND_ONE_BY_ID = "SELECT * FROM EMPLOYEE WHERE ID = ?";


    public EmployeeDao(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    public Long insertNew(Employee employee) {
        var simpleInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("EMPLOYEE")
                .usingGeneratedKeyColumns("ID");

        var params = SqlParametersMapBuilder.builder()
                .with("NAME", employee.getName())
                .withIfNotNull("DEPARTMENT_ID", employee.getDepartment().map(Department::getId)
                        .orElse(null))
                .build();

        Number id = simpleInsert.executeAndReturnKey(params);

        return id.longValue();
    }

    public Employee insertNewAndGet(Employee employee) {
        var id = this.insertNew(employee);
        return this.findOneLazyById(id);
    }

    public List<Employee> findAll() {
        return jdbcTemplate.query(FIND_ALL, Employee.ROW_MAPPER_LAZY);
    }

    public List<Employee> findAllByDepartmentId(Long depId) {
        return jdbcTemplate.query(FIND_ALL_BY_DEPARTMENT_ID, new Object[]{depId}, Employee.ROW_MAPPER_LAZY);
    }

    public Employee findOneLazyById(Long id) {
        return jdbcTemplate.queryForObject(FIND_ONE_BY_ID, new Object[]{id}, Employee.ROW_MAPPER_LAZY);
    }

}

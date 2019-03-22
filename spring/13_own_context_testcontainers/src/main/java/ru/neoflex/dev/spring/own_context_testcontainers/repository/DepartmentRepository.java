package ru.neoflex.dev.spring.own_context_testcontainers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.dev.spring.own_context_testcontainers.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}

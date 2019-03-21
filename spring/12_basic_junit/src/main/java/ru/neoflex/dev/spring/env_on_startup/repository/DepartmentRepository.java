package ru.neoflex.dev.spring.env_on_startup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.dev.spring.env_on_startup.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}

package ru.neoflex.dev.spring.entity_graphs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.dev.spring.entity_graphs.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

}

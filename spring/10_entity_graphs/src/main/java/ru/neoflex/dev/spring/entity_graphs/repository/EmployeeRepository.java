package ru.neoflex.dev.spring.entity_graphs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.dev.spring.entity_graphs.entity.Employee;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Stream<Employee> findAllAsStreamBy();
    Optional<Employee> findMaybeOneById(Long id);
    List<Employee> findTop2ByOrderByAgeDesc();
}

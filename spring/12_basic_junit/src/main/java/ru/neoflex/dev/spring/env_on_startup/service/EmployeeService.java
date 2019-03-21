package ru.neoflex.dev.spring.env_on_startup.service;

import org.springframework.stereotype.Service;
import ru.neoflex.dev.spring.env_on_startup.entity.Employee;
import ru.neoflex.dev.spring.env_on_startup.repository.EmployeeRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return this.employeeRepository.findAll();
    }

    public Optional<Employee> findById(Long empId) {
        return this.employeeRepository.findById(empId);
    }
}

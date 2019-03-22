package ru.neoflex.dev.spring.mocks.service;

import org.springframework.stereotype.Service;
import ru.neoflex.dev.spring.mocks.entity.Department;
import ru.neoflex.dev.spring.mocks.repository.DepartmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository employeeRepository) {
        this.departmentRepository = employeeRepository;
    }

    public List<Department> findAll() {
        System.out.println("TEST-TEST");
        return this.departmentRepository.findAll();
    }

    public Optional<Department> findById(Long empId) {
        return this.departmentRepository.findById(empId);
    }
}
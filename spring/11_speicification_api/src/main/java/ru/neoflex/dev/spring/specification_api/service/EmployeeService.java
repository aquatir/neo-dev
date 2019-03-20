package ru.neoflex.dev.spring.specification_api.service;

import org.springframework.stereotype.Service;
import ru.neoflex.dev.spring.specification_api.entity.Employee;
import ru.neoflex.dev.spring.specification_api.repository.EmployeeRepository;
import ru.neoflex.dev.spring.specification_api.specification.EmployeeSpecifications;

import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> allOlderThen20() {
        return this.employeeRepository.findAll(EmployeeSpecifications.olderThen20);
    }

    public List<Employee> allInFRIR() {
        return this.employeeRepository.findAll(EmployeeSpecifications.workInDepartmentFRIR);
    }

    public List<Employee> allOlderThen20InFRIR() {
        return this.employeeRepository.findAll(EmployeeSpecifications.workInDepartmentFRIR
                .and(EmployeeSpecifications.olderThen20));
    }
}

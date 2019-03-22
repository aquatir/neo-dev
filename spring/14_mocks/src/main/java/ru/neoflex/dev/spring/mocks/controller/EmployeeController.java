package ru.neoflex.dev.spring.mocks.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.dev.spring.mocks.dto.EmployeeDto;
import ru.neoflex.dev.spring.mocks.service.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employee/{id}")
    public EmployeeDto findOneById(@PathVariable Long id) {
        var emp = this.employeeService.findById(id).orElseThrow();
        return EmployeeDto.ofEmployee(emp);
    }

    @GetMapping("/employee")
    public List<EmployeeDto> findAll() {
        System.out.println("TEST-TEST");
        var emps = this.employeeService.findAll();
        var empDtos = emps.stream().map(EmployeeDto::ofEmployee).collect(Collectors.toList());
        return empDtos;
    }
}

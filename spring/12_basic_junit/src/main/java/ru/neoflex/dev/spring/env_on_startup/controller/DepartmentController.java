package ru.neoflex.dev.spring.env_on_startup.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.dev.spring.env_on_startup.dto.DepartmentDto;
import ru.neoflex.dev.spring.env_on_startup.service.DepartmentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/department/{id}")
    public DepartmentDto findOneById(@PathVariable Long id) {
        var department = this.departmentService.findById(id).orElseThrow();
        return DepartmentDto.ofDepartment(department);
    }

    @GetMapping("/department")
    public List<DepartmentDto> findAll() {
        var departments = this.departmentService.findAll();
        var departmentDtos = departments.stream().map(DepartmentDto::ofDepartment).collect(Collectors.toList());
        return departmentDtos;
    }
}
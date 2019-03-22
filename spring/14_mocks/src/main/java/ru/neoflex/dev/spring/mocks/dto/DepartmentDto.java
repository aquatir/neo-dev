package ru.neoflex.dev.spring.mocks.dto;

import ru.neoflex.dev.spring.mocks.entity.Department;

public class DepartmentDto {
    private Long id;
    private String name;

    public DepartmentDto() {};

    public static DepartmentDto ofDepartment(Department department) {
        return DepartmentDto.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static DepartmentDtoBuilder builder() {
        return new DepartmentDtoBuilder();
    }

    public static final class DepartmentDtoBuilder {
        private Long id;
        private String name;

        private DepartmentDtoBuilder() {
        }

        public DepartmentDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public DepartmentDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DepartmentDto build() {
            DepartmentDto departmentDto = new DepartmentDto();
            departmentDto.id = this.id;
            departmentDto.name = this.name;
            return departmentDto;
        }
    }


}

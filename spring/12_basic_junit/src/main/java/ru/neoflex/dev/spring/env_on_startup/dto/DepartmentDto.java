package ru.neoflex.dev.spring.env_on_startup.dto;

public class DepartmentDto {
    private Long id;
    private String name;

    public DepartmentDto() {};


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

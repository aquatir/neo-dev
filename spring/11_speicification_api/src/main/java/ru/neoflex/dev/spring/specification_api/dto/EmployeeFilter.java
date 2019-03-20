package ru.neoflex.dev.spring.specification_api.dto;

import java.util.List;

public class EmployeeFilter {
    private Long ageFrom;
    private Long ageTo;

    private List<String> departments;

    public Long getAgeFrom() {
        return ageFrom;
    }

    public Long getAgeTo() {
        return ageTo;
    }

    public List<String> getDepartments() {
        return departments;
    }

    public static EmployeeFilterBuilder builder() {
        return new EmployeeFilterBuilder();
    }

    public static final class EmployeeFilterBuilder {
        private Long ageFrom;
        private Long ageTo;
        private List<String> departments;

        private EmployeeFilterBuilder() {
        }

        public EmployeeFilterBuilder ageFrom(Long ageFrom) {
            this.ageFrom = ageFrom;
            return this;
        }

        public EmployeeFilterBuilder ageTo(Long ageTo) {
            this.ageTo = ageTo;
            return this;
        }

        public EmployeeFilterBuilder departments(List<String> departments) {
            this.departments = departments;
            return this;
        }

        public EmployeeFilter build() {
            EmployeeFilter employeeFilter = new EmployeeFilter();
            employeeFilter.ageFrom = this.ageFrom;
            employeeFilter.ageTo = this.ageTo;
            employeeFilter.departments = this.departments;
            return employeeFilter;
        }
    }
}

package ru.neoflex.dev.spring.specification_api.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.neoflex.dev.spring.specification_api.dto.EmployeeFilter;
import ru.neoflex.dev.spring.specification_api.entity.Employee;

import javax.persistence.criteria.JoinType;

public class EmployeeSpecifications {

    public static Specification<Employee> olderThen20 =
            (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("age"), 20);

    public static Specification<Employee> workInDepartmentFRIR =
            (root, query, criteriaBuilder) -> {

                var fetch = root.fetch("department", JoinType.LEFT);
                return criteriaBuilder.equal(root.get("department").get("name"), "FRIR");
            };


    public static Specification<Employee> ofEmployeeFilter(EmployeeFilter employeeFilter) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();
            var andExpr = predicate.getExpressions();

            return predicate;
        };
    }
}

package ru.neoflex.dev.spring.entity_graphs.service;

import org.springframework.stereotype.Service;
import ru.neoflex.dev.spring.entity_graphs.entity.City;
import ru.neoflex.dev.spring.entity_graphs.entity.Department;
import ru.neoflex.dev.spring.entity_graphs.entity.Employee;
import ru.neoflex.dev.spring.entity_graphs.repository.CityRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CityService {
    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    public String printAllEmfsInAllCities() {
        var unstructuredCities = this.cityRepository.findAllBy();

        Map<City, Map<Department, List<Employee>>> result = unstructuredCities.stream().collect(Collectors.groupingBy(
                entryAsCity -> new City(
                        bigIntegerAsLong(entryAsCity[0]),
                        (String) entryAsCity[1]),
                Collectors.groupingBy(
                        entryAsDepartment -> new Department(
                                bigIntegerAsLong(entryAsDepartment[2]),
                                (String) entryAsDepartment[3]),
                        Collectors
                                .mapping(entryAsEmployee -> new Employee(
                                                bigIntegerAsLong(entryAsEmployee[4]),
                                                (String) entryAsEmployee[5],
                                                bigIntegerAsLong(entryAsEmployee[6])),
                                        Collectors.toList()))
        ));

        for (City city : result.keySet()) {
            var listOfDeps = new HashSet<Department>();
            for (Department department : result.get(city).keySet()) {

                var listOfEmps = new HashSet<Employee>();
                for (Employee employee : result.get(city).get(department)) {
                    employee.setDepartment(department);
                    listOfEmps.add(employee);

                }
                department.setCity(city);
                department.setEmployees(listOfEmps);
                listOfDeps.add(department);
            }
            city.setDepartments(listOfDeps);
        }

        var cities = result.keySet();

        var emps = cities.stream().map(City::getDepartments)
                .flatMap(Set::stream)
                .map(Department::getEmployees)
                .flatMap(Set::stream)
                .map(Employee::getName)
                .collect(Collectors.joining(","));

        return emps;
    }

    private Long bigIntegerAsLong(Object object) {
        return ((BigInteger) object).longValue();
    }
}

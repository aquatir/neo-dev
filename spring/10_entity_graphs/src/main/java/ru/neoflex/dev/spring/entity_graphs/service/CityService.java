package ru.neoflex.dev.spring.entity_graphs.service;

import org.springframework.stereotype.Service;
import ru.neoflex.dev.spring.entity_graphs.entity.City;
import ru.neoflex.dev.spring.entity_graphs.entity.Department;
import ru.neoflex.dev.spring.entity_graphs.entity.Employee;
import ru.neoflex.dev.spring.entity_graphs.repository.CityRepository;

import javax.transaction.Transactional;
import java.math.BigInteger;
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
        var cities = this.cityRepository.findAllBy();

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

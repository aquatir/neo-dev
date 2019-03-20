package ru.neoflex.dev.spring.specification_api.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import ru.neoflex.dev.spring.specification_api.dto.EmployeeFilter;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EmployeeServiceTest {
    @Autowired
    private EmployeeService employeeService;

    @Test
    public void test_getAllOlderThen20() {
        var olderThen20 = employeeService.allOlderThen20();

        Assert.assertTrue(olderThen20.stream().allMatch(emp -> emp.getAge() > 20));
        Assert.assertEquals(2, olderThen20.size());
    }

    @Test
    public void test_getAllInFrir() {
        var inFrir = employeeService.allInFRIR();

        Assert.assertTrue(inFrir.stream().allMatch(emp -> emp.getDepartment().getName().equals("FRIR")));
        Assert.assertEquals(2, inFrir.size());
    }

    @Test
    public void test_getAllInFrirAndOlderThen20() {
        var olderThen20InFrir = employeeService.allOlderThen20InFRIR();

        Assert.assertTrue(olderThen20InFrir.stream().allMatch(emp -> emp.getDepartment().getName().equals("FRIR")));
        Assert.assertTrue(olderThen20InFrir.stream().allMatch(emp -> emp.getAge() > 20));
        Assert.assertEquals(1, olderThen20InFrir.size());
    }

    @Test
    public void test_byFilterFrom19to35() {
        var filter = EmployeeFilter
                .builder()
                .ageFrom(19L)
                .ageTo(35L)
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertTrue(empMatchFilter.stream().allMatch(emp -> emp.getAge() >= 19 && emp.getAge() <= 35));
        Assert.assertEquals(2, empMatchFilter.size());
    }


    @Test
    public void test_byFilterFrom19toNotSpecified() {
        var filter = EmployeeFilter
                .builder()
                .ageFrom(19L)
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertTrue(empMatchFilter.stream().allMatch(emp -> emp.getAge() >= 19));
        Assert.assertEquals(3, empMatchFilter.size());
    }

    @Test
    public void test_byFilterFromNotSpecifiedTo24() {
        var filter = EmployeeFilter
                .builder()
                .ageTo(25L)
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertTrue(empMatchFilter.stream().allMatch(emp -> emp.getAge() <= 25));
        Assert.assertEquals(3, empMatchFilter.size());
    }

    @Test
    public void test_byFilter_noParameters() {
        var filter = EmployeeFilter
                .builder()
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertEquals(4, empMatchFilter.size());
    }

    @Test
    public void test_byFilter_ByDepartmentFIRI() {
        var filter = EmployeeFilter
                .builder()
                .departments(List.of("FRIR"))
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertTrue(empMatchFilter.stream().allMatch(emp -> emp.getDepartment().getName().equals("FRIR")));
        Assert.assertEquals(2, empMatchFilter.size());
    }

    @Test
    public void test_byFilter_ByTwoDepsNoMatch() {
        var filter = EmployeeFilter
                .builder()
                .departments(List.of("NON EXISTING"))
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertEquals(0, empMatchFilter.size());
    }

    @Test
    public void test_byFilter_FrirAndAnotherOne() {
        var filter = EmployeeFilter
                .builder()
                .departments(List.of("NON EXISTING", "FRIR"))
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertTrue(empMatchFilter.stream().allMatch(emp -> emp.getDepartment().getName().equals("FRIR")));
        Assert.assertEquals(2, empMatchFilter.size());
    }

    @Test
    public void test_byFilter_FrirAndNFOOne() {
        var filter = EmployeeFilter
                .builder()
                .departments(List.of("NFO", "FRIR"))
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertEquals(4, empMatchFilter.size());
    }

    @Test
    public void test_byFilter_ByAgeAndDepartment() {
        var filter = EmployeeFilter
                .builder()
                .departments(List.of("FRIR"))
                .ageFrom(22L)
                .ageTo(25L)
                .build();
        var empMatchFilter = employeeService.byAgeAndMaybeOneOfDepartmentNames(filter);

        Assert.assertTrue(empMatchFilter.stream().allMatch(emp -> emp.getDepartment().getName().equals("FRIR")));
        Assert.assertTrue(empMatchFilter.stream().allMatch(emp -> emp.getAge() >= 22 && emp.getAge() <= 25));
        Assert.assertEquals(1, empMatchFilter.size());
    }
}

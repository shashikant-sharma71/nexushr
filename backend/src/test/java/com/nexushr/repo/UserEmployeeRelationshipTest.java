package com.nexushr.repo;

import com.nexushr.model.Department;
import com.nexushr.model.Employee;
import com.nexushr.model.Role;
import com.nexushr.model.User;
import com.nexushr.dto.EmployeeDtos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserEmployeeRelationshipTest {
    @Autowired UserRepository users;
    @Autowired EmployeeRepository employees;
    @Autowired DepartmentRepository departments;

    @Test
    void adminDirectoryQueryLoadsEmployeeDepartmentAndManager() {
        Department department = departments.save(new Department("Engineering", "Engineering team"));
        Employee manager = employee("M-001", "Manager", "manager@test.local", department, null);
        manager = employees.save(manager);
        Employee member = employee("E-001", "Member", "member@test.local", department, manager);
        member = employees.save(member);
        User user = new User(member.name, member.email, "encoded", Role.EMPLOYEE);
        user.employee = member;
        users.save(user);

        User loaded = users.findAllWithEmployeeDetails().stream()
                .filter(candidate -> candidate.email.equals("member@test.local"))
                .findFirst().orElseThrow();

        assertThat(loaded.employee).isNotNull();
        assertThat(loaded.employee.id).isEqualTo(member.id);
        assertThat(loaded.employee.designation).isEqualTo("Engineer");
        assertThat(loaded.employee.department.name).isEqualTo("Engineering");
        assertThat(loaded.employee.manager.name).isEqualTo("Manager");
    }

    @Test
    void managerTeamQueryReturnsOnlyEmployeesAssignedToRequestedManager() {
        Employee managerA = employees.save(employee("M-101", "Manager A", "a@test.local", null, null));
        Employee managerB = employees.save(employee("M-102", "Manager B", "b@test.local", null, null));
        Employee a1 = employees.save(employee("E-101", "A1", "a1@test.local", null, managerA));
        employees.save(employee("E-102", "B1", "b1@test.local", null, managerB));

        assertThat(employees.findByManagerId(managerA.id))
                .extracting(employee -> employee.id)
                .containsExactly(a1.id);
    }

    @Test
    void managerLookupFetchesLinkedEmployeeProfile() {
        Employee profile = employees.save(employee("M-201", "Manager C", "c@test.local", null, null));
        User user = new User("Manager C", "c@test.local", "encoded", Role.MANAGER);
        user.employee = profile;
        users.save(user);

        User loaded = users.findWithEmployeeByEmailIgnoreCase("C@TEST.LOCAL").orElseThrow();

        assertThat(loaded.employee).isNotNull();
        assertThat(loaded.employee.id).isEqualTo(profile.id);
    }

    @Test
    void authenticatedUserQueryFetchesDepartmentAndManagerForProfileDto() {
        Department department = departments.save(new Department("Platform", "Platform team"));
        Employee manager = employees.save(employee("M-250", "Manager E", "me@test.local", department, null));
        Employee member = employees.save(employee("E-250", "Member E", "ee@test.local", department, manager));
        User user = new User(member.name, member.email, "encoded", Role.EMPLOYEE);
        user.employee = member;
        users.save(user);

        User loaded = users.findWithEmployeeByEmailIgnoreCase(member.email).orElseThrow();
        EmployeeDtos.Profile profile = EmployeeDtos.Profile.from(loaded.employee);

        assertThat(profile.departmentName()).isEqualTo("Platform");
        assertThat(profile.managerName()).isEqualTo("Manager E");
    }

    @Test
    void employeeProfileDtoContainsOnlyStableProfileFields() {
        Department department = departments.save(new Department("People", "People team"));
        Employee manager = employees.save(employee("M-301", "Manager D", "md@test.local", department, null));
        Employee member = employees.save(employee("E-301", "Member D", "ed@test.local", department, manager));

        EmployeeDtos.Profile profile = EmployeeDtos.Profile.from(member);

        assertThat(profile.id()).isEqualTo(member.id);
        assertThat(profile.departmentName()).isEqualTo("People");
        assertThat(profile.managerName()).isEqualTo("Manager D");
        assertThat(profile.employeeCode()).isEqualTo("E-301");
    }

    private Employee employee(String code, String name, String email, Department department, Employee manager) {
        Employee employee = new Employee();
        employee.employeeCode = code;
        employee.name = name;
        employee.email = email;
        employee.designation = "Engineer";
        employee.department = department;
        employee.manager = manager;
        return employee;
    }
}

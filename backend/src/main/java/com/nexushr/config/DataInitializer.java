package com.nexushr.config;

import com.nexushr.model.*;
import com.nexushr.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.LocalDate;

@Configuration
@Profile("!prod")
public class DataInitializer {
    @Bean
    CommandLineRunner init(UserRepository users, DepartmentRepository deps,
                           EmployeeRepository emps, PasswordEncoder enc,
                           PlatformTransactionManager transactionManager) {
        return args -> new TransactionTemplate(transactionManager)
                .executeWithoutResult(status -> initialize(users, deps, emps, enc));
    }

    void initialize(UserRepository users, DepartmentRepository deps,
                    EmployeeRepository emps, PasswordEncoder enc) {
        Department eng = deps.findAll().stream().filter(d -> "Engineering".equals(d.name))
                .findFirst().orElseGet(() -> deps.save(new Department("Engineering", "Product engineering and technology")));
        Department hr = deps.findAll().stream().filter(d -> "Human Resources".equals(d.name))
                .findFirst().orElseGet(() -> deps.save(new Department("Human Resources", "People operations")));

        Employee manager = emps.findByEmailIgnoreCase("manager@nexushr.local").orElseGet(() -> {
            Employee e = new Employee(); e.employeeCode = "EMP1001"; e.name = "Aarav Sharma";
            e.email = "manager@nexushr.local"; e.designation = "Engineering Manager";
            e.joiningDate = LocalDate.now().minusMonths(18); e.salary = 95000.0; e.department = eng;
            return emps.save(e);
        });
        manager.department = eng;
        manager = emps.save(manager);

        Employee employee = emps.findByEmailIgnoreCase("employee@nexushr.local").orElseGet(() -> {
            Employee e = new Employee(); e.employeeCode = "EMP1002"; e.name = "Demo Employee";
            e.email = "employee@nexushr.local"; e.designation = "Software Engineer";
            e.joiningDate = LocalDate.now().minusMonths(8); e.salary = 65000.0;
            return emps.save(e);
        });
        employee.department = eng;
        employee.manager = manager;
        employee = emps.save(employee);

        Employee hrEmployee = emps.findByEmailIgnoreCase("hr@nexushr.local").orElseGet(() -> {
            Employee e = new Employee(); e.employeeCode = "EMP1003"; e.name = "Neha Verma";
            e.email = "hr@nexushr.local"; e.designation = "HR Executive";
            e.joiningDate = LocalDate.now().minusMonths(14); e.salary = 52000.0;
            return emps.save(e);
        });
        hrEmployee.department = hr;
        hrEmployee.manager = null;
        hrEmployee = emps.save(hrEmployee);

        ensureUser(users, enc, "System Admin", "admin@nexushr.local", "Admin@123", Role.ADMIN, null);
        ensureUser(users, enc, "Neha Verma", "hr@nexushr.local", "Hr@12345", Role.HR, hrEmployee);
        ensureUser(users, enc, "Aarav Sharma", "manager@nexushr.local", "Manager@123", Role.MANAGER, manager);
        ensureUser(users, enc, "Demo Employee", "employee@nexushr.local", "Employee@123", Role.EMPLOYEE, employee);
    }

    private void ensureUser(UserRepository users, PasswordEncoder enc, String name,
                            String email, String password, Role role, Employee employee) {
        User user = users.findByEmailIgnoreCase(email).orElseGet(() ->
                new User(name, email, enc.encode(password), role));
        boolean changed = false;
        if (user.role != role) { user.role = role; changed = true; }
        if (employee == null && user.employee != null) { user.employee = null; changed = true; }
        if (employee != null && (user.employee == null || !employee.id.equals(user.employee.id))) {
            user.employee = employee; changed = true;
        }
        if (changed || user.id == null) users.save(user);
    }
}

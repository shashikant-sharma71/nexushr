package com.nexushr.dto;

import com.nexushr.model.Employee;
import java.time.LocalDate;

public final class EmployeeDtos {
    private EmployeeDtos() {}

    public record Request(String name, String email, String phone, String designation,
                          LocalDate joiningDate, String employmentType, Double salary,
                          Long departmentId, Long managerId) {}

    /** Stable API representation; never exposes Hibernate proxies or entity internals. */
    public record Profile(Long id, String employeeCode, String name, String email,
                          String phone, String designation, LocalDate joiningDate,
                          String employmentType, Double salary, String status,
                          Long departmentId, String departmentName,
                          Long managerId, String managerName) {
        public static Profile from(Employee employee) {
            if (employee == null) return null;
            return new Profile(
                    employee.id, employee.employeeCode, employee.name, employee.email,
                    employee.phone, employee.designation, employee.joiningDate,
                    employee.employmentType == null ? null : employee.employmentType.name(),
                    employee.salary,
                    employee.status == null ? null : employee.status.name(),
                    employee.department == null ? null : employee.department.id,
                    employee.department == null ? null : employee.department.name,
                    employee.manager == null ? null : employee.manager.id,
                    employee.manager == null ? null : employee.manager.name
            );
        }
    }
}

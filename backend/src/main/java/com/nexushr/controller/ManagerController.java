package com.nexushr.controller;

import com.nexushr.model.*;
import com.nexushr.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {
    private final UserRepository users;
    private final EmployeeRepository employees;
    private final LeaveRequestRepository leaves;
    private final AttendanceRepository attendance;
    private final PerformanceReviewRepository performance;

    public ManagerController(UserRepository users, EmployeeRepository employees, LeaveRequestRepository leaves,
                             AttendanceRepository attendance, PerformanceReviewRepository performance) {
        this.users = users; this.employees = employees; this.leaves = leaves;
        this.attendance = attendance; this.performance = performance;
    }

    private Employee manager(Authentication authentication) {
        User user = users.findWithEmployeeByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.employee == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager profile missing");
        return user.employee;
    }

    private Map<String,Object> employeeView(Employee e) {
        Map<String,Object> view = new LinkedHashMap<>();
        view.put("id", e.id); view.put("employeeCode", e.employeeCode); view.put("name", e.name);
        view.put("email", e.email); view.put("designation", e.designation); view.put("phone", e.phone);
        view.put("joiningDate", e.joiningDate); view.put("employmentType", e.employmentType);
        view.put("status", e.status);
        view.put("department", e.department == null ? null : Map.of("id", e.department.id, "name", e.department.name));
        return view;
    }

    @Transactional(readOnly = true)
    @GetMapping("/team")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public List<Map<String,Object>> team(Authentication auth) {
        return employees.findByManagerId(manager(auth).id).stream().map(this::employeeView).toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/attendance")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public List<Map<String,Object>> teamAttendance(Authentication auth) {
        return attendance.findByEmployeeManagerIdOrderByWorkDateDesc(manager(auth).id).stream().map(a -> {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("id", a.id); row.put("workDate", a.workDate); row.put("checkIn", a.checkIn);
            row.put("checkOut", a.checkOut); row.put("status", a.status);
            row.put("employee", employeeView(a.employee)); return row;
        }).toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/leaves")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public List<Map<String,Object>> teamLeaves(Authentication auth) {
        return leaves.findByEmployeeManagerIdOrderByCreatedAtDesc(manager(auth).id).stream().map(x -> {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("id", x.id); row.put("startDate", x.startDate); row.put("endDate", x.endDate);
            row.put("type", x.type); row.put("reason", x.reason); row.put("status", x.status);
            row.put("createdAt", x.createdAt); row.put("employee", employeeView(x.employee)); return row;
        }).toList();
    }

    @Transactional
    @PutMapping("/leaves/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public Map<String,Object> status(Authentication auth, @PathVariable Long id, @RequestParam LeaveStatus value) {
        LeaveRequest request = leaves.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Leave request not found"));
        if (request.employee.manager == null || !request.employee.manager.id.equals(manager(auth).id))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your team member");
        if (request.status != LeaveStatus.PENDING)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only pending leave requests can be reviewed");
        if (value != LeaveStatus.APPROVED && value != LeaveStatus.REJECTED)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid leave decision");
        request.status = (value == LeaveStatus.APPROVED ? LeaveStatus.MANAGER_APPROVED : value);
        LeaveRequest saved = leaves.save(request);
        Map<String,Object> result = new LinkedHashMap<>(); result.put("id", saved.id); result.put("status", saved.status);
        result.put("startDate", saved.startDate); result.put("endDate", saved.endDate);
        result.put("employee", employeeView(saved.employee)); return result;
    }

    @Transactional(readOnly = true)
    @GetMapping("/performance")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MANAGER')")
    public List<Map<String,Object>> teamPerformance(Authentication auth) {
        return performance.findByEmployeeManagerIdOrderByReviewDateDesc(manager(auth).id).stream().map(x -> {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("id", x.id); row.put("reviewDate", x.reviewDate); row.put("technicalSkills", x.technicalSkills);
            row.put("communication", x.communication); row.put("teamwork", x.teamwork);
            row.put("productivity", x.productivity); row.put("leadership", x.leadership);
            row.put("overallScore", x.overallScore); row.put("feedback", x.feedback);
            row.put("employee", employeeView(x.employee)); return row;
        }).toList();
    }
}

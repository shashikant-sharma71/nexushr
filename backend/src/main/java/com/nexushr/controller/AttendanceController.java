package com.nexushr.controller;

import com.nexushr.dto.AttendanceCorrectionRequest;
import com.nexushr.model.Attendance;
import com.nexushr.model.AttendanceAudit;
import com.nexushr.repo.AttendanceRepository;
import com.nexushr.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('ADMIN','HR')")
public class AttendanceController {
    private final AttendanceRepository attendance;
    private final AttendanceService service;
    public AttendanceController(AttendanceRepository attendance, AttendanceService service) {
        this.attendance = attendance; this.service = service;
    }
    @GetMapping public List<Attendance> all() { return attendance.findAll(); }
    @PostMapping("/check-in/{employeeId}")
    public Attendance checkIn(@PathVariable Long employeeId) { return service.adminCheckIn(employeeId); }
    @PostMapping("/check-out/{employeeId}")
    public Attendance checkOut(@PathVariable Long employeeId) { return service.adminCheckOut(employeeId); }
    @PatchMapping("/{attendanceId}/correction")
    public Attendance correct(@PathVariable Long attendanceId, @Valid @RequestBody AttendanceCorrectionRequest request,
                              Authentication authentication) {
        return service.correct(attendanceId, request, authentication.getName());
    }
    @GetMapping("/{attendanceId}/history")
    public List<AttendanceAudit> history(@PathVariable Long attendanceId) { return service.history(attendanceId); }
}

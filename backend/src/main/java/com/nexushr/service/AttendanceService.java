package com.nexushr.service;

import com.nexushr.dto.AttendanceCorrectionRequest;
import com.nexushr.model.*;
import com.nexushr.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendance;
    private final EmployeeRepository employees;
    private final UserRepository users;
    private final AttendanceAuditRepository audits;

    public AttendanceService(AttendanceRepository attendance, EmployeeRepository employees,
                             UserRepository users, AttendanceAuditRepository audits) {
        this.attendance = attendance; this.employees = employees; this.users = users; this.audits = audits;
    }

    @Transactional
    public Attendance adminCheckIn(Long employeeId) {
        Employee employee = employees.findByIdForUpdate(employeeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        return checkInLocked(employee);
    }

    @Transactional
    public Attendance selfCheckIn(String email) {
        User user = currentUser(email);
        if (user.employee == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee profile is linked to this account");
        Employee employee = employees.findByIdForUpdate(user.employee.id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        return checkInLocked(employee);
    }

    private Attendance checkInLocked(Employee employee) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        Attendance record = attendance.findByEmployeeIdAndWorkDate(employee.id, today).orElseGet(() -> {
            Attendance fresh = new Attendance(); fresh.employee = employee; fresh.workDate = today; return fresh;
        });
        if (record.checkIn != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Already checked in today");
        record.checkIn = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        record.status = record.checkIn.toLocalTime().isAfter(LocalTime.of(9, 30)) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
        return attendance.save(record);
    }

    @Transactional
    public Attendance adminCheckOut(Long employeeId) {
        Employee employee = employees.findByIdForUpdate(employeeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        return checkOutLocked(employee);
    }

    @Transactional
    public Attendance selfCheckOut(String email) {
        User user = currentUser(email);
        if (user.employee == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No employee profile is linked to this account");
        Employee employee = employees.findByIdForUpdate(user.employee.id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        return checkOutLocked(employee);
    }

    private Attendance checkOutLocked(Employee employee) {
        Attendance record = attendance.findByEmployeeIdAndWorkDate(employee.id, LocalDate.now(ZoneId.of("Asia/Kolkata")))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check in first"));
        if (record.checkIn == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check in first");
        if (record.checkOut != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Already checked out today");
        record.checkOut = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        return attendance.save(record);
    }

    @Transactional
    public Attendance correct(Long attendanceId, AttendanceCorrectionRequest request, String actorEmail) {
        Attendance snapshot = attendance.findById(attendanceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance record not found"));
        employees.findByIdForUpdate(snapshot.employee.id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        Attendance record = attendance.findByIdForUpdate(attendanceId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance record not found"));
        User actor = currentUser(actorEmail);
        if (request.checkIn() == null && request.checkOut() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide at least one timestamp to correct");
        LocalDateTime nextIn = request.checkIn() != null ? request.checkIn() : record.checkIn;
        LocalDateTime nextOut = request.checkOut() != null ? request.checkOut() : record.checkOut;
        if (nextOut != null && nextIn == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-in is required before check-out");
        if (request.checkIn() != null && !request.checkIn().toLocalDate().equals(record.workDate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corrected check-in must match the attendance work date");
        if (request.checkOut() != null && !request.checkOut().toLocalDate().equals(record.workDate))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corrected check-out must match the attendance work date");
        if (nextIn != null && nextOut != null && nextOut.isBefore(nextIn))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out cannot be earlier than check-in");

        LocalDateTime changedAt = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        if (request.checkIn() != null && !request.checkIn().equals(record.checkIn)) {
            audits.save(new AttendanceAudit(record.id, "checkIn", string(record.checkIn), string(request.checkIn()),
                request.reason().trim(), actor.id, actor.email, actor.name, changedAt));
            record.checkIn = request.checkIn();
        }
        if (request.checkOut() != null && !request.checkOut().equals(record.checkOut)) {
            audits.save(new AttendanceAudit(record.id, "checkOut", string(record.checkOut), string(request.checkOut()),
                request.reason().trim(), actor.id, actor.email, actor.name, changedAt));
            record.checkOut = request.checkOut();
        }
        if (record.checkIn != null)
            record.status = record.checkIn.toLocalTime().isAfter(LocalTime.of(9, 30)) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT;
        return attendance.save(record);
    }

    @Transactional(readOnly = true)
    public List<AttendanceAudit> history(Long attendanceId) {
        if (!attendance.existsById(attendanceId)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance record not found");
        return audits.findByAttendanceIdOrderByChangedAtDesc(attendanceId);
    }

    private User currentUser(String email) {
        return users.findWithEmployeeByEmailIgnoreCase(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
    private String string(LocalDateTime value) { return value == null ? null : value.toString(); }
}

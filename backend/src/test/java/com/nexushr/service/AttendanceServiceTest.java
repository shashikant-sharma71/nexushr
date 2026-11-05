package com.nexushr.service;

import com.nexushr.dto.AttendanceCorrectionRequest;
import com.nexushr.model.*;
import com.nexushr.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {
    @Mock AttendanceRepository attendance;
    @Mock EmployeeRepository employees;
    @Mock UserRepository users;
    @Mock AttendanceAuditRepository audits;
    @InjectMocks AttendanceService service;
    Employee employee;
    User admin;

    @BeforeEach void setup() {
        employee = new Employee(); employee.id = 17L; employee.name = "Test Employee";
        admin = new User("Admin", "admin@test.local", "unused", Role.ADMIN); admin.id = 1L;
    }

    @Test void checkInCreatesTodaysRecord() {
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByEmployeeIdAndWorkDate(eq(17L), any(LocalDate.class))).thenReturn(Optional.empty());
        when(attendance.save(any())).thenAnswer(i -> i.getArgument(0));
        Attendance result = service.adminCheckIn(17L);
        assertNotNull(result.checkIn); assertEquals(employee, result.employee); assertNotNull(result.status);
    }

    @Test void duplicateCheckInReturnsConflictAndDoesNotOverwrite() {
        Attendance existing = new Attendance(); existing.checkIn = LocalDateTime.now().minusHours(2);
        LocalDateTime original = existing.checkIn;
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByEmployeeIdAndWorkDate(eq(17L), any(LocalDate.class))).thenReturn(Optional.of(existing));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.adminCheckIn(17L));
        assertEquals(409, ex.getStatusCode().value()); assertEquals(original, existing.checkIn); verify(attendance, never()).save(any());
    }

    @Test void missingEmployeeReturns404() {
        when(employees.findByIdForUpdate(99L)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.adminCheckIn(99L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test void checkOutWithoutCheckInReturns400() {
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByEmployeeIdAndWorkDate(eq(17L), any(LocalDate.class))).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.adminCheckOut(17L));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test void duplicateCheckOutReturns409() {
        Attendance existing = new Attendance(); existing.checkIn = LocalDateTime.now().minusHours(8); existing.checkOut = LocalDateTime.now();
        LocalDateTime original = existing.checkOut;
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByEmployeeIdAndWorkDate(eq(17L), any(LocalDate.class))).thenReturn(Optional.of(existing));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.adminCheckOut(17L));
        assertEquals(409, ex.getStatusCode().value()); assertEquals(original, existing.checkOut);
    }

    @Test void correctionCreatesAuditWithAuthenticatedActor() {
        Attendance record = new Attendance(); record.id = 5L; record.employee = employee; record.workDate = LocalDate.of(2026, 11, 3);
        LocalDateTime newIn = LocalDateTime.of(2026, 11, 3, 9, 0);
        when(attendance.findById(5L)).thenReturn(Optional.of(record));
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByIdForUpdate(5L)).thenReturn(Optional.of(record));
        when(users.findWithEmployeeByEmailIgnoreCase("admin@test.local")).thenReturn(Optional.of(admin));
        when(attendance.save(any())).thenAnswer(i -> i.getArgument(0));
        service.correct(5L, new AttendanceCorrectionRequest(newIn, null, "Manager approved correction"), "admin@test.local");
        assertEquals(newIn, record.checkIn);
        ArgumentCaptor<AttendanceAudit> captor = ArgumentCaptor.forClass(AttendanceAudit.class);
        verify(audits).save(captor.capture());
        assertEquals(1L, captor.getValue().actorUserId); assertEquals("checkIn", captor.getValue().changedField);
        assertEquals("Manager approved correction", captor.getValue().reason);
    }

    @Test void correctionRejectsCheckOutBeforeCheckIn() {
        Attendance record = new Attendance(); record.id = 5L; record.employee = employee; record.workDate = LocalDate.of(2026, 11, 3); record.checkIn = LocalDateTime.of(2026, 11, 3, 10, 0);
        when(attendance.findById(5L)).thenReturn(Optional.of(record));
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByIdForUpdate(5L)).thenReturn(Optional.of(record));
        when(users.findWithEmployeeByEmailIgnoreCase("admin@test.local")).thenReturn(Optional.of(admin));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.correct(5L,
            new AttendanceCorrectionRequest(null, LocalDateTime.of(2026, 11, 3, 9, 0), "Fix"), "admin@test.local"));
        assertEquals(400, ex.getStatusCode().value()); verify(audits, never()).save(any());
    }
    @Test void correctionRejectsTimestampOnDifferentWorkDate() {
        Attendance record = new Attendance(); record.id = 5L; record.employee = employee; record.workDate = LocalDate.of(2026, 11, 3);
        when(attendance.findById(5L)).thenReturn(Optional.of(record));
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByIdForUpdate(5L)).thenReturn(Optional.of(record));
        when(users.findWithEmployeeByEmailIgnoreCase("admin@test.local")).thenReturn(Optional.of(admin));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.correct(5L,
            new AttendanceCorrectionRequest(LocalDateTime.of(2026, 11, 4, 9, 0), null, "Fix"), "admin@test.local"));
        assertEquals(400, ex.getStatusCode().value()); verify(audits, never()).save(any());
    }

    @Test void correctionRequiresAtLeastOneTimestamp() {
        Attendance record = new Attendance(); record.id = 5L; record.employee = employee; record.workDate = LocalDate.of(2026, 11, 3);
        when(attendance.findById(5L)).thenReturn(Optional.of(record));
        when(employees.findByIdForUpdate(17L)).thenReturn(Optional.of(employee));
        when(attendance.findByIdForUpdate(5L)).thenReturn(Optional.of(record));
        when(users.findWithEmployeeByEmailIgnoreCase("admin@test.local")).thenReturn(Optional.of(admin));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.correct(5L,
            new AttendanceCorrectionRequest(null, null, "No timestamp supplied"), "admin@test.local"));
        assertEquals(400, ex.getStatusCode().value()); verify(audits, never()).save(any());
    }

}

package com.nexushr.repo;

import com.nexushr.model.AttendanceAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AttendanceAuditRepository extends JpaRepository<AttendanceAudit, Long> {
    List<AttendanceAudit> findByAttendanceIdOrderByChangedAtDesc(Long attendanceId);
}

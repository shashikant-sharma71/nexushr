package com.nexushr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_audit")
public class AttendanceAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false) public Long attendanceId;
    @Column(nullable = false, length = 32) public String changedField;
    @Column(length = 40) public String oldValue;
    @Column(nullable = false, length = 40) public String newValue;
    @Column(nullable = false, length = 500) public String reason;
    @Column(nullable = false) public Long actorUserId;
    @Column(nullable = false, length = 190) public String actorEmail;
    @Column(nullable = false, length = 255) public String actorName;
    @Column(nullable = false) public LocalDateTime changedAt;
    protected AttendanceAudit() {}

    public AttendanceAudit(Long attendanceId, String changedField, String oldValue, String newValue,
                           String reason, Long actorUserId, String actorEmail, String actorName,
                           LocalDateTime changedAt) {
        this.attendanceId = attendanceId;
        this.changedField = changedField;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.reason = reason;
        this.actorUserId = actorUserId;
        this.actorEmail = actorEmail;
        this.actorName = actorName;
        this.changedAt = changedAt;
    }
}

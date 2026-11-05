package com.nexushr.model;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="leave_requests") public class LeaveRequest { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Employee employee; public LocalDate startDate,endDate; public String type,reason; @Enumerated(EnumType.STRING) public LeaveStatus status=LeaveStatus.PENDING; public LocalDateTime createdAt=LocalDateTime.now(); }

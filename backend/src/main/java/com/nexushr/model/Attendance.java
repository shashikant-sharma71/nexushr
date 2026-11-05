package com.nexushr.model;
import jakarta.persistence.*; import java.time.*;
@Entity @Table(name="attendance",uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","work_date"})) public class Attendance { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Employee employee; @Column(name="work_date") public LocalDate workDate; public LocalDateTime checkIn,checkOut; @Enumerated(EnumType.STRING) public AttendanceStatus status; public Attendance(){} }

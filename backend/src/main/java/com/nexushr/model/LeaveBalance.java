package com.nexushr.model;
import jakarta.persistence.*;
@Entity @Table(name="leave_balances",uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","leave_year"})) public class LeaveBalance { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @OneToOne(optional=false) public Employee employee; @Column(name="leave_year") public int year; public int casualLeave=6,sickLeave=4,earnedLeave=10; }

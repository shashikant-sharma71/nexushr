package com.nexushr.model;
import jakarta.persistence.*; import java.time.YearMonth;
@Entity @Table(name="payrolls",uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","period"})) public class Payroll { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Employee employee; @Column(nullable=false) public String period; public double basicSalary,hra,allowances,grossSalary,pf,tax,otherDeductions,netSalary; public Payroll(){} }

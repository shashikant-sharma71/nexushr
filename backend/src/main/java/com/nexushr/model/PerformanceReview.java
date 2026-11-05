package com.nexushr.model;
import jakarta.persistence.*; import java.time.LocalDate;
@Entity @Table(name="performance_reviews") public class PerformanceReview { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @ManyToOne(optional=false) public Employee employee; public LocalDate reviewDate=LocalDate.now(); public double technicalSkills,communication,teamwork,productivity,leadership,overallScore; public String feedback; }

package com.nexushr.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="candidates")
public class Candidate {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(nullable=false) public String email;
 public String phone;
 @Column(nullable=false) public String position;
 @Column(nullable=false) public String status="APPLIED";
 public String source;
 @Column(length=2000) public String notes;
 @Column(nullable=false) public LocalDateTime appliedAt=LocalDateTime.now();
}

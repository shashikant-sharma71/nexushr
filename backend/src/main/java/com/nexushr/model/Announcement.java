package com.nexushr.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="announcements")
public class Announcement {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String title;
 @Column(nullable=false,length=4000) public String message;
 @Column(nullable=false) public String audience="ALL";
 @Column(nullable=false) public LocalDateTime createdAt=LocalDateTime.now();
 @Column(nullable=false) public String createdBy;
}

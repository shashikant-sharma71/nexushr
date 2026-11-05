package com.nexushr.model;
import jakarta.persistence.*; import java.time.LocalDateTime;
@Entity @Table(name="notifications") public class Notification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(optional=false) public User user;
 @ManyToOne @JoinColumn(name="announcement_id") public Announcement announcement;
 public String title,message; public boolean read=false; public LocalDateTime createdAt=LocalDateTime.now();
}

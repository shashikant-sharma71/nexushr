package com.nexushr.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="holidays", uniqueConstraints=@UniqueConstraint(columnNames={"holiday_date"}))
public class Holiday {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @Column(nullable=false) public String name;
 @Column(name="holiday_date",nullable=false) public LocalDate date;
 public String description;
 public boolean optionalHoliday=false;
}

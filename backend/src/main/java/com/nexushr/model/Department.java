package com.nexushr.model;
import jakarta.persistence.*;
@Entity @Table(name="departments") public class Department { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id; @Column(nullable=false,unique=true) public String name; public String description; public Department(){} public Department(String n,String d){name=n;description=d;} }

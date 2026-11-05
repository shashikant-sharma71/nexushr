package com.nexushr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public Long id;
    @Column(unique=true, nullable=false, length=190)
    public String email;
    @Column(nullable=false)
    public String password;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    public Role role;
    @Column(nullable=false)
    public String name;
    @Column(nullable=false, columnDefinition="boolean default true")
    public boolean active=true;
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="employee_id", unique=true)
    public Employee employee;
    @Column(nullable=false)
    public long tokenVersion=0L;
    @Column(nullable=false)
    public LocalDateTime createdAt=LocalDateTime.now();
    public User(){}
    public User(String n,String e,String p,Role r){name=n;email=e;password=p;role=r;}
}

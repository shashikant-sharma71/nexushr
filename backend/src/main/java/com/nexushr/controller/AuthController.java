package com.nexushr.controller;
import com.nexushr.dto.AuthDtos.*; import com.nexushr.model.*; import com.nexushr.repo.UserRepository; import com.nexushr.repo.EmployeeRepository; import com.nexushr.security.JwtService;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.web.bind.annotation.*; import org.springframework.transaction.annotation.Transactional;
@RestController @RequestMapping("/api/auth") public class AuthController {
 final UserRepository users; final EmployeeRepository employees; final PasswordEncoder enc; final JwtService jwt;
 public AuthController(UserRepository u,EmployeeRepository employees,PasswordEncoder e,JwtService j){users=u;this.employees=employees;enc=e;jwt=j;}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody Login x){var u=users.findWithEmployeeByEmailIgnoreCase(x.email()).orElseThrow(()->new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials")); if(!u.active||!enc.matches(x.password(),u.password))throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"); return new AuthResponse(jwt.generate(u.email,u.role.name(),u.tokenVersion),u.name,u.email,u.role.name(),u.employee==null?null:u.employee.id);}
 @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','HR')") @Transactional
 @PostMapping("/register") public AuthResponse register(@Valid @RequestBody Register x){
  if(users.findByEmailIgnoreCase(x.email()).isPresent())throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT,"Email already exists");
  Employee employee=new Employee(); employee.name=x.name().trim(); employee.email=x.email().trim().toLowerCase(); employee.employeeCode="EMP"+java.util.UUID.randomUUID().toString().replace("-","").substring(0,10).toUpperCase(); employee.joiningDate=java.time.LocalDate.now(); employee.salary=0.0;
  employee=employees.save(employee); User u=new User(x.name().trim(),x.email().trim().toLowerCase(),enc.encode(x.password()),Role.EMPLOYEE);u.employee=employee;u=users.save(u);
  return new AuthResponse(jwt.generate(u.email,u.role.name(),u.tokenVersion),u.name,u.email,u.role.name(),employee.id);
 }
}

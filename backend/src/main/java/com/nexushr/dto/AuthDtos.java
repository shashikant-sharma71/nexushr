package com.nexushr.dto;
import jakarta.validation.constraints.*;
public class AuthDtos {
 public record Login(@NotBlank @Email String email,@NotBlank @Size(min=8,max=128) String password){}
 public record Register(@NotBlank @Size(max=100) String name,@NotBlank @Email String email,@NotBlank @Size(min=8,max=128) String password,String role){}
 public record AuthResponse(String token,String name,String email,String role,Long employeeId){}
}

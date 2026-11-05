package com.nexushr.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.security.Keys; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Service;
import javax.crypto.SecretKey; import java.nio.charset.StandardCharsets; import java.time.Duration; import java.util.Date;
@Service public class JwtService {
 private final SecretKey key; private final long expiration;
 public JwtService(@Value("${nexushr.jwt.secret}") String secret,@Value("${nexushr.jwt.expiration-ms}") long exp){
  if(secret==null||secret.length()<32) throw new IllegalStateException("JWT_SECRET must be at least 32 characters");
  key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); expiration=exp;
 }
 public String generate(String email,String role){return generate(email,role,0L);}
 public String generate(String email,String role,long tokenVersion){Date now=new Date();return Jwts.builder().issuer("nexushr-api").subject(email).claim("role",role).claim("tokenVersion",tokenVersion).issuedAt(now).expiration(new Date(now.getTime()+expiration)).signWith(key).compact();}
 public Claims parse(String token){return Jwts.parser().verifyWith(key).requireIssuer("nexushr-api").build().parseSignedClaims(token).getPayload();}
}

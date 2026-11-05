package com.nexushr.security;
import jakarta.servlet.*; import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component; import com.nexushr.repo.UserRepository; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.List;
@Component public class JwtAuthFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final UserRepository users; public JwtAuthFilter(JwtService j,UserRepository users){jwt=j;this.users=users;}
 @Override protected void doFilterInternal(HttpServletRequest r,HttpServletResponse s,FilterChain f)throws ServletException,IOException{
  String h=r.getHeader("Authorization");
  if(h!=null&&h.startsWith("Bearer ")){try{var c=jwt.parse(h.substring(7));String role=c.get("role",String.class);Object versionClaim=c.get("tokenVersion");long tokenVersion=versionClaim instanceof Number n?n.longValue():-1L;if(role!=null&&tokenVersion>=0&&users.findByEmailIgnoreCase(c.getSubject()).filter(u->u.active&&u.role.name().equals(role)&&u.tokenVersion==tokenVersion).isPresent())SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(c.getSubject(),null,List.of(new SimpleGrantedAuthority("ROLE_"+role))));}catch(Exception ignored){} }
  f.doFilter(r,s);
 }
}

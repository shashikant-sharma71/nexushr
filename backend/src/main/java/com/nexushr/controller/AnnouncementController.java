package com.nexushr.controller;
import com.nexushr.model.*;
import com.nexushr.repo.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/announcements")
public class AnnouncementController {
 private final AnnouncementRepository repo; private final UserRepository users; private final NotificationRepository notifications;
 public AnnouncementController(AnnouncementRepository repo,UserRepository users,NotificationRepository notifications){this.repo=repo;this.users=users;this.notifications=notifications;}
 @GetMapping public List<Announcement> all(Authentication auth){String role=auth.getAuthorities().stream().findFirst().map(a->a.getAuthority().replace("ROLE_","")).orElse("EMPLOYEE");return repo.findAllByOrderByCreatedAtDesc().stream().filter(a->"ALL".equals(a.audience)||role.equals(a.audience)).toList();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','HR')") public Announcement create(@RequestBody Announcement x,Authentication auth){
  if(x.title==null||x.title.isBlank()||x.message==null||x.message.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Title and message are required");
  if(x.audience==null||!List.of("ALL","ADMIN","HR","MANAGER","EMPLOYEE").contains(x.audience))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid audience");
  x.id=null;x.createdAt=java.time.LocalDateTime.now();x.createdBy=auth.getName();Announcement saved=repo.save(x);
  users.findAll().stream().filter(u->"ALL".equals(saved.audience)||u.role.name().equals(saved.audience)).forEach(u->{Notification n=new Notification();n.user=u;n.announcement=saved;n.title="New announcement: "+saved.title;n.message=saved.message.length()>240?saved.message.substring(0,237)+"...":saved.message;n.read=false;notifications.save(n);});
  return saved;
 }
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Announcement not found");repo.deleteById(id);}
}

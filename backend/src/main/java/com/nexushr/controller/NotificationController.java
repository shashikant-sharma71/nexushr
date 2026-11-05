package com.nexushr.controller;
import com.nexushr.model.Notification;
import com.nexushr.repo.NotificationRepository;
import com.nexushr.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/notifications")
public class NotificationController {
 private final NotificationRepository notifications; private final UserRepository users;
 public NotificationController(NotificationRepository n,UserRepository u){notifications=n;users=u;}
 private Map<String,Object> view(Notification n){Map<String,Object> out=new LinkedHashMap<>();out.put("id",n.id);out.put("title",n.title);out.put("message",n.message);out.put("read",n.read);out.put("createdAt",n.createdAt);out.put("announcementId",n.announcement==null?null:n.announcement.id);return out;}
 @GetMapping public List<Map<String,Object>> mine(Authentication auth){var user=users.findByEmailIgnoreCase(auth.getName()).orElseThrow();return notifications.findByUserIdOrderByCreatedAtDesc(user.id).stream().map(this::view).toList();}
 @GetMapping("/unread-count") public Map<String,Long> unreadCount(Authentication auth){var user=users.findByEmailIgnoreCase(auth.getName()).orElseThrow();return Map.of("count",notifications.countByUserIdAndReadFalse(user.id));}
 @PatchMapping("/{id}/read") public Map<String,Object> markRead(@PathVariable Long id,Authentication auth){var user=users.findByEmailIgnoreCase(auth.getName()).orElseThrow();Notification n=notifications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification not found"));if(!n.user.id.equals(user.id))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not your notification");n.read=true;return view(notifications.save(n));}
 @PatchMapping("/announcement/{announcementId}/read") public Map<String,Object> markAnnouncementRead(@PathVariable Long announcementId,Authentication auth){var user=users.findByEmailIgnoreCase(auth.getName()).orElseThrow();Notification n=notifications.findByUserIdAndAnnouncementId(user.id,announcementId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Announcement notification not found"));n.read=true;notifications.save(n);return Map.of("read",true,"announcementId",announcementId);}
}

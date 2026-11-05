package com.nexushr.repo;
import com.nexushr.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface NotificationRepository extends JpaRepository<Notification,Long>{
 List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
 long countByUserIdAndReadFalse(Long userId);
 Optional<Notification> findByUserIdAndAnnouncementId(Long userId, Long announcementId);
}

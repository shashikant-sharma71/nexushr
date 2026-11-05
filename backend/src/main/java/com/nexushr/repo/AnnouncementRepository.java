package com.nexushr.repo;
import com.nexushr.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> { List<Announcement> findAllByOrderByCreatedAtDesc(); }

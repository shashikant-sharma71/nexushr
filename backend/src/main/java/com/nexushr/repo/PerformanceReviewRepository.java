package com.nexushr.repo;
import com.nexushr.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview,Long>{
    List<PerformanceReview> findByEmployeeIdOrderByReviewDateDesc(Long employeeId);
    List<PerformanceReview> findByEmployeeManagerIdOrderByReviewDateDesc(Long managerId);
}

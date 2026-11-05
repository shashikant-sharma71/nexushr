package com.nexushr.repo;
import com.nexushr.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long>{
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long id);
    List<LeaveRequest> findByEmployeeManagerIdOrderByCreatedAtDesc(Long managerId);
    long countByStatus(LeaveStatus s);
}

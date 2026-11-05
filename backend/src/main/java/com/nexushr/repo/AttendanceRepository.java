package com.nexushr.repo;
import com.nexushr.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.*;
public interface AttendanceRepository extends JpaRepository<Attendance,Long>{
    Optional<Attendance> findByEmployeeIdAndWorkDate(Long employeeId,LocalDate date);
    List<Attendance> findByEmployeeIdOrderByWorkDateDesc(Long employeeId);
    List<Attendance> findByEmployeeManagerIdOrderByWorkDateDesc(Long managerId);
    long countByStatus(AttendanceStatus status);
    long countByStatusAndWorkDate(AttendanceStatus status, LocalDate date);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Attendance a where a.id = :id")
    Optional<Attendance> findByIdForUpdate(@Param("id") Long id);
}

package com.nexushr.repo;
import com.nexushr.model.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PayrollRepository extends JpaRepository<Payroll,Long>{
    List<Payroll> findByEmployeeIdOrderByPeriodDesc(Long employeeId);
    Optional<Payroll> findByEmployeeIdAndPeriod(Long employeeId,String period);
    long countByEmployeeManagerId(Long managerId);
}

package com.nexushr.repo;
import com.nexushr.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface EmployeeRepository extends JpaRepository<Employee,Long>{
    Optional<Employee> findByEmailIgnoreCase(String email);
    @EntityGraph(attributePaths = {"department"})
    @Query("select e from Employee e where e.manager.id = :managerId order by e.id")
    List<Employee> findByManagerId(@Param("managerId") Long managerId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Employee e where e.id = :id")
    Optional<Employee> findByIdForUpdate(@Param("id") Long id);
}

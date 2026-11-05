package com.nexushr.repo;
import com.nexushr.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByEmailIgnoreCase(String email);

    long countByRole(com.nexushr.model.Role role);

    /** Fetches the complete user-to-employee graph used by the Admin user directory. */
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.manager"})
    @Query("select u from User u order by u.id")
    java.util.List<User> findAllWithEmployeeDetails();

    /** Loads the authenticated manager profile before the repository transaction closes. */
    @EntityGraph(attributePaths = {"employee", "employee.department", "employee.manager"})
    @Query("select u from User u where lower(u.email) = lower(:email)")
    Optional<User> findWithEmployeeByEmailIgnoreCase(String email);
}

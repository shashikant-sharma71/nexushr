package com.nexushr.repo;
import com.nexushr.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CandidateRepository extends JpaRepository<Candidate,Long> {}

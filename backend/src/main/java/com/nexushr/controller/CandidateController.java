package com.nexushr.controller;
import com.nexushr.model.Candidate;
import com.nexushr.repo.CandidateRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/recruitment") @PreAuthorize("hasAnyRole('ADMIN','HR')")
public class CandidateController {
 private final CandidateRepository repo;
 public CandidateController(CandidateRepository repo){this.repo=repo;}
 @GetMapping public List<Candidate> all(){return repo.findAll();}
 @PostMapping public Candidate create(@Valid @RequestBody Candidate x){x.id=null;x.status="APPLIED";x.appliedAt=java.time.LocalDateTime.now();return repo.save(x);}
 @PutMapping("/{id}") public Candidate update(@PathVariable Long id,@RequestBody Candidate x){Candidate c=repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Candidate not found"));c.name=x.name;c.email=x.email;c.phone=x.phone;c.position=x.position;c.source=x.source;c.notes=x.notes;if(x.status!=null&&List.of("APPLIED","SCREENING","INTERVIEW","OFFERED","HIRED","REJECTED").contains(x.status))c.status=x.status;return repo.save(c);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Candidate not found");repo.deleteById(id);}
}

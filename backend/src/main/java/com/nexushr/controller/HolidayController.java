package com.nexushr.controller;
import com.nexushr.model.Holiday;
import com.nexushr.repo.HolidayRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@RestController @RequestMapping("/api/holidays")
public class HolidayController {
 private final HolidayRepository repo;
 public HolidayController(HolidayRepository repo){this.repo=repo;}
 @GetMapping public List<Holiday> all(){return repo.findAllByOrderByDateAsc();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','HR')") public Holiday create(@RequestBody Holiday x){x.id=null;if(x.date==null||x.name==null||x.name.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Holiday name and date are required");return repo.save(x);}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public void delete(@PathVariable Long id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Holiday not found");repo.deleteById(id);}
}

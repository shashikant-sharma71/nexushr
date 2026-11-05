package com.nexushr.controller;
import com.nexushr.model.*; import com.nexushr.dto.EmployeeDtos; import com.nexushr.repo.*; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.time.*; import java.util.*;
import org.springframework.web.server.ResponseStatusException;

@RestController @RequestMapping("/api/me") public class MeController {
 final UserRepository users; final AttendanceRepository attendance; final LeaveRequestRepository leaves; final PayrollRepository payroll; final PerformanceReviewRepository performance; final com.nexushr.service.AttendanceService attendanceService;
 public MeController(UserRepository u,AttendanceRepository a,LeaveRequestRepository l,PayrollRepository p,PerformanceReviewRepository r,com.nexushr.service.AttendanceService attendanceService){users=u;attendance=a;leaves=l;payroll=p;performance=r;this.attendanceService=attendanceService;}
 User user(Authentication a){return users.findWithEmployeeByEmailIgnoreCase(a.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found"));}
 Employee employee(Authentication a){var u=user(a);if(u.employee==null)throw new ResponseStatusException(HttpStatus.CONFLICT,"No employee profile is linked to this account; contact HR to complete your profile");return u.employee;}
 @GetMapping public Map<String,Object> me(Authentication a){var u=user(a);Map<String,Object> out=new LinkedHashMap<>();out.put("id",u.id);out.put("name",u.name);out.put("email",u.email);out.put("role",u.role);out.put("employee",EmployeeDtos.Profile.from(u.employee));return out;}
 @GetMapping("/attendance") public List<Attendance> attendance(Authentication a){return attendance.findByEmployeeIdOrderByWorkDateDesc(employee(a).id);}
 @PostMapping("/attendance/check-in") public Attendance checkIn(Authentication a){return attendanceService.selfCheckIn(a.getName());}
 @PostMapping("/attendance/check-out") public Attendance checkOut(Authentication a){return attendanceService.selfCheckOut(a.getName());}
 @GetMapping("/leaves") public List<LeaveRequest> leaves(Authentication a){return leaves.findByEmployeeIdOrderByCreatedAtDesc(employee(a).id);}
 @PostMapping("/leaves") public LeaveRequest createLeave(Authentication a,@RequestBody LeaveRequest x){if(x.startDate==null||x.endDate==null||x.endDate.isBefore(x.startDate))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid leave dates");x.employee=employee(a);x.status=LeaveStatus.PENDING;x.createdAt=LocalDateTime.now();return leaves.save(x);}
 @GetMapping("/payroll") public List<Payroll> payroll(Authentication a){return payroll.findByEmployeeIdOrderByPeriodDesc(employee(a).id);}
 @GetMapping("/performance") public List<PerformanceReview> performance(Authentication a){return performance.findByEmployeeIdOrderByReviewDateDesc(employee(a).id);}
}

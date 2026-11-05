package com.nexushr.controller;
import com.nexushr.repo.*;import org.springframework.http.HttpStatus;import org.springframework.web.server.ResponseStatusException;import com.nexushr.model.*;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;import java.time.LocalDate;import java.util.*;
@RestController @RequestMapping("/api/dashboard") public class DashboardController{
 final EmployeeRepository e;final AttendanceRepository a;final LeaveRequestRepository l;final PayrollRepository p;final UserRepository users;
 public DashboardController(EmployeeRepository e,AttendanceRepository a,LeaveRequestRepository l,PayrollRepository p,UserRepository u){this.e=e;this.a=a;this.l=l;this.p=p;this.users=u;}
 @GetMapping public Map<String,Object> stats(Authentication auth){var u=users.findWithEmployeeByEmailIgnoreCase(auth.getName()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found"));Map<String,Object> m=new LinkedHashMap<>();
  if((u.role==Role.EMPLOYEE||u.role==Role.MANAGER)&& (u.employee==null||u.employee.id==null)) throw new ResponseStatusException(HttpStatus.CONFLICT,"No employee profile is linked to this account; contact HR to complete your profile");
  if(u.role==Role.EMPLOYEE&&u.employee!=null){var id=u.employee.id;var today=a.findByEmployeeIdAndWorkDate(id,LocalDate.now(java.time.ZoneId.of("Asia/Kolkata")));m.put("employees",1);m.put("present",today.filter(x->x.status==AttendanceStatus.PRESENT).isPresent()?1:0);m.put("late",today.filter(x->x.status==AttendanceStatus.LATE).isPresent()?1:0);m.put("onLeave",l.findByEmployeeIdOrderByCreatedAtDesc(id).stream().filter(x->x.status==LeaveStatus.APPROVED).count());m.put("pendingLeaves",l.findByEmployeeIdOrderByCreatedAtDesc(id).stream().filter(x->x.status==LeaveStatus.PENDING).count());m.put("payrollRecords",p.findByEmployeeIdOrderByPeriodDesc(id).size());return m;}
  if(u.role==Role.MANAGER&&u.employee!=null){var team=e.findByManagerId(u.employee.id);m.put("employees",team.size());m.put("present",a.findByEmployeeManagerIdOrderByWorkDateDesc(u.employee.id).stream().filter(x->x.status==AttendanceStatus.PRESENT&&x.workDate.equals(LocalDate.now(java.time.ZoneId.of("Asia/Kolkata")))).count());m.put("late",a.findByEmployeeManagerIdOrderByWorkDateDesc(u.employee.id).stream().filter(x->x.status==AttendanceStatus.LATE&&x.workDate.equals(LocalDate.now(java.time.ZoneId.of("Asia/Kolkata")))).count());m.put("onLeave",l.findByEmployeeManagerIdOrderByCreatedAtDesc(u.employee.id).stream().filter(x->x.status==LeaveStatus.APPROVED).count());m.put("pendingLeaves",l.findByEmployeeManagerIdOrderByCreatedAtDesc(u.employee.id).stream().filter(x->x.status==LeaveStatus.PENDING).count());m.put("payrollRecords",p.countByEmployeeManagerId(u.employee.id));return m;}
  m.put("employees",e.count());m.put("present",a.countByStatusAndWorkDate(AttendanceStatus.PRESENT,LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"))));m.put("late",a.countByStatusAndWorkDate(AttendanceStatus.LATE,LocalDate.now(java.time.ZoneId.of("Asia/Kolkata"))));m.put("onLeave",l.countByStatus(LeaveStatus.APPROVED));m.put("pendingLeaves",l.countByStatus(LeaveStatus.PENDING)+l.countByStatus(LeaveStatus.MANAGER_APPROVED));m.put("payrollRecords",p.count());return m;
 }
 @GetMapping("/ai-risk") @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN','HR')") public List<Map<String,Object>> risk(){
  List<Map<String,Object>> out=new ArrayList<>(); LocalDate cutoff=LocalDate.now(java.time.ZoneId.of("Asia/Kolkata")).minusDays(30);
  for(var employee:e.findAll()){
   long lateCount=a.findByEmployeeIdOrderByWorkDateDesc(employee.id).stream().filter(x->x.workDate!=null&&!x.workDate.isBefore(cutoff)&&x.status==AttendanceStatus.LATE).count();
   double score=Math.min(.9,lateCount/10.0); String level=score>=.6?"HIGH":score>=.3?"MEDIUM":"LOW";
   String recommendation=lateCount>0?"Review recent attendance entries with the employee and check for recording errors.":"No recent late-attendance pattern detected; continue normal check-ins.";
   out.add(Map.of("employeeId",employee.id,"employee",employee.name,"probability",score,"risk",level,"lateAttendanceCount",lateCount,"recommendation",recommendation));
  } return out;
 }

}

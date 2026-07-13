package com.example.business.api;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.business.api.dto.CreateCustomerRequest;
import com.example.business.api.dto.CreateProjectRequest;
import com.example.business.api.dto.CreateTaskRequest;
import com.example.business.api.dto.CustomerDto;
import com.example.business.api.dto.ProjectDto;
import com.example.business.api.dto.TaskDto;
import com.example.business.domain.Customer;
import com.example.business.domain.CustomerRepository;
import com.example.business.domain.Project;
import com.example.business.domain.ProjectRepository;
import com.example.business.domain.TaskItem;
import com.example.business.domain.TaskRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/business")
@Validated
@RequiredArgsConstructor
public class BusinessController {

  private final CustomerRepository customerRepo;
  private final ProjectRepository projectRepo;
  private final TaskRepository taskRepo;

  @GetMapping("/customers")
  public ResponseEntity<?> myCustomers(org.springframework.security.core.Authentication auth) {
    Long userId = userId(auth);
    if (userId == null) return ResponseEntity.status(401).build();
    return ResponseEntity.ok(customerRepo.findAllByUserIdOrderByIdDesc(userId).stream().map(BusinessMapper::toDto).toList());
  }

  @GetMapping("/customers/{id}")
  public ResponseEntity<?> getCustomer(org.springframework.security.core.Authentication auth, @PathVariable String id) {
    Long userId = userId(auth);
    Long cid = tryParseLong(id);
    if (userId == null || cid == null) return ResponseEntity.badRequest().build();
    Customer c = customerRepo.findById(cid).orElse(null);
    if (c == null || !c.getUserId().equals(userId)) return ResponseEntity.status(404).build();
    return ResponseEntity.ok(BusinessMapper.toDto(c));
  }

  @PostMapping("/customers")
  public ResponseEntity<CustomerDto> createCustomer(
      org.springframework.security.core.Authentication auth,
      @Valid @RequestBody CreateCustomerRequest req
  ) {
    Long userId = userId(auth);
    if (userId == null) return ResponseEntity.status(401).build();
    Customer c = new Customer();
    c.setUserId(userId);
    c.setName(req.name().trim());
    c.setEmail(req.email() == null ? null : req.email().trim());
    c.setPhone(req.phone() == null ? null : req.phone().trim());
    c.setAddress(req.address() == null ? null : req.address().trim());
    c = customerRepo.save(c);
    return ResponseEntity.ok(BusinessMapper.toDto(c));
  }

  @PutMapping("/customers/{id}")
  public ResponseEntity<CustomerDto> updateCustomer(
      org.springframework.security.core.Authentication auth,
      @PathVariable String id,
      @Valid @RequestBody CreateCustomerRequest req
  ) {
    Long userId = userId(auth);
    Long cid = tryParseLong(id);
    if (userId == null || cid == null) return ResponseEntity.badRequest().build();
    Customer c = customerRepo.findById(cid).orElse(null);
    if (c == null || !c.getUserId().equals(userId)) return ResponseEntity.status(404).build();
    c.setName(req.name().trim());
    c.setEmail(req.email() == null ? null : req.email().trim());
    c.setPhone(req.phone() == null ? null : req.phone().trim());
    c.setAddress(req.address() == null ? null : req.address().trim());
    c = customerRepo.save(c);
    return ResponseEntity.ok(BusinessMapper.toDto(c));
  }

  @DeleteMapping("/customers/{id}")
  public ResponseEntity<Void> deleteCustomer(org.springframework.security.core.Authentication auth, @PathVariable String id) {
    Long userId = userId(auth);
    Long cid = tryParseLong(id);
    if (userId == null) return ResponseEntity.status(401).build();
    if (cid == null) return ResponseEntity.badRequest().build();
    Customer c = customerRepo.findById(cid).orElse(null);
    if (c == null || !c.getUserId().equals(userId)) return ResponseEntity.status(404).build();
    customerRepo.deleteById(cid);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/projects")
  public ResponseEntity<?> myProjects(org.springframework.security.core.Authentication auth) {
    Long userId = userId(auth);
    if (userId == null) return ResponseEntity.status(401).build();
    return ResponseEntity.ok(projectRepo.findAllByUserIdOrderByIdDesc(userId).stream().map(BusinessMapper::toDto).toList());
  }

  @PostMapping("/projects")
  public ResponseEntity<ProjectDto> createProject(
      org.springframework.security.core.Authentication auth,
      @Valid @RequestBody CreateProjectRequest req
  ) {
    Long userId = userId(auth);
    if (userId == null) return ResponseEntity.status(401).build();
    Project p = new Project();
    p.setUserId(userId);
    p.setName(req.name().trim());
    p.setDescription(req.description() == null ? null : req.description().trim());
    p = projectRepo.save(p);
    return ResponseEntity.ok(BusinessMapper.toDto(p));
  }

  @DeleteMapping("/projects/{id}")
  public ResponseEntity<Void> deleteProject(org.springframework.security.core.Authentication auth, @PathVariable String id) {
    Long userId = userId(auth);
    Long pid = tryParseLong(id);
    if (userId == null) return ResponseEntity.status(401).build();
    if (pid == null) return ResponseEntity.badRequest().build();
    Project p = projectRepo.findById(pid).orElse(null);
    if (p == null || !p.getUserId().equals(userId)) return ResponseEntity.status(404).build();
    projectRepo.deleteById(pid);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/projects/{projectId}/tasks")
  public ResponseEntity<?> tasks(
      org.springframework.security.core.Authentication auth,
      @PathVariable String projectId
  ) {
    Long userId = userId(auth);
    Long pid = tryParseLong(projectId);
    if (userId == null) return ResponseEntity.status(401).build();
    if (pid == null) return ResponseEntity.badRequest().build();
    return ResponseEntity.ok(taskRepo.findAllByUserIdAndProjectIdOrderByIdDesc(userId, pid).stream().map(BusinessMapper::toDto).toList());
  }

  @PostMapping("/tasks")
  public ResponseEntity<TaskDto> createTask(
      org.springframework.security.core.Authentication auth,
      @Valid @RequestBody CreateTaskRequest req
  ) {
    Long userId = userId(auth);
    Long pid = tryParseLong(req.projectId());
    if (userId == null) return ResponseEntity.status(401).build();
    if (pid == null) return ResponseEntity.badRequest().build();
    TaskItem t = new TaskItem();
    t.setUserId(userId);
    t.setProjectId(pid);
    t.setTitle(req.title().trim());
    t = taskRepo.save(t);
    return ResponseEntity.ok(BusinessMapper.toDto(t));
  }

  @PutMapping("/tasks/{id}/toggle")
  public ResponseEntity<TaskDto> toggleTask(org.springframework.security.core.Authentication auth, @PathVariable String id) {
    Long userId = userId(auth);
    Long tid = tryParseLong(id);
    if (userId == null) return ResponseEntity.status(401).build();
    if (tid == null) return ResponseEntity.badRequest().build();
    TaskItem t = taskRepo.findById(tid).orElse(null);
    if (t == null || !t.getUserId().equals(userId)) return ResponseEntity.status(404).build();
    t.setDone(!t.isDone());
    t = taskRepo.save(t);
    return ResponseEntity.ok(BusinessMapper.toDto(t));
  }

  private static Long userId(org.springframework.security.core.Authentication auth) {
    if (auth == null || auth.getName() == null) return null;
    try {
      return Long.parseLong(auth.getName());
    } catch (Exception e) {
      return null;
    }
  }

  private static Long tryParseLong(String v) {
    try {
      return Long.parseLong(v);
    } catch (Exception e) {
      return null;
    }
  }
}


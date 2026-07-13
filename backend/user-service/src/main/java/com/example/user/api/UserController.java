package com.example.user.api;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.api.dto.CreateUserRequest;
import com.example.user.api.dto.UpsertMeRequest;
import com.example.user.api.dto.UserDto;
import com.example.user.user.AppUser;
import com.example.user.user.AppUserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {

  private final AppUserRepository repo;

  @PostMapping("/internal")
  public ResponseEntity<Void> createUserInternal(@RequestBody CreateUserRequest req) {
    if (repo.existsById(req.id())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    AppUser user = new AppUser();
    user.setId(req.id());
    user.setEmail(req.email());
    user.setDisplayName(req.displayName());
    user.setCreatedAt(Instant.now());
    user.setUpdatedAt(Instant.now());
    repo.save(user);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/me")
  public ResponseEntity<UserDto> me(org.springframework.security.core.Authentication auth) {
    Long userId = userId(auth);
    if (userId == null) return ResponseEntity.status(401).build();
    return repo.findById(userId)
        .map(UserMapper::toDto)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(404).build());
  }

  @PutMapping("/me")
  public ResponseEntity<UserDto> upsertMe(
      org.springframework.security.core.Authentication auth,
      @Valid @RequestBody UpsertMeRequest req
  ) {
    Long userId = userId(auth);
    if (userId == null) return ResponseEntity.status(401).build();

    AppUser u = repo.findById(userId).orElseGet(AppUser::new);
    u.setId(userId);
    u.setEmail(req.email().trim().toLowerCase());
    u.setDisplayName(req.displayName().trim());
    u.setUpdatedAt(Instant.now());
    u = repo.save(u);
    return ResponseEntity.ok(UserMapper.toDto(u));
  }

  private static Long userId(org.springframework.security.core.Authentication auth) {
    if (auth == null || auth.getName() == null) return null;
    try {
      return Long.parseLong(auth.getName());
    } catch (Exception e) {
      return null;
    }
  }
}


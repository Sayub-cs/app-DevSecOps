package com.example.auth.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.auth.api.dto.AuthResponse;
import com.example.auth.api.dto.CreateUserRequest;
import com.example.auth.api.dto.LoginRequest;
import com.example.auth.api.dto.MeResponse;
import com.example.auth.api.dto.RegisterRequest;
import com.example.auth.security.JwtService;
import com.example.auth.user.AuthUser;
import com.example.auth.user.AuthUserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  private final AuthUserRepository repo;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final WebClient userServiceWebClient;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
    if (repo.existsByEmailIgnoreCase(req.email())) {
      return ResponseEntity.badRequest().build();
    }

    AuthUser u = new AuthUser();
    u.setEmail(req.email().trim().toLowerCase());
    u.setDisplayName(req.displayName().trim());
    u.setPasswordHash(passwordEncoder.encode(req.password()));
    u.getRoles().add("USER");
    u = repo.save(u);

    // Sync user to user-service
    CreateUserRequest createUserReq = new CreateUserRequest(
        u.getId(), u.getEmail(), u.getDisplayName()
    );

    try {
      userServiceWebClient.post()
          .uri("/api/users/internal")
          .bodyValue(createUserReq)
          .retrieve()
          .bodyToMono(Void.class)
          .block();
      log.info("User {} synced to user-service successfully", u.getId());
    } catch (Exception e) {
      log.error("Failed to sync user {} to user-service: {}", u.getId(), e.getMessage());
    }

    return ResponseEntity.ok(toAuthResponse(u));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
    AuthUser u = repo.findByEmailIgnoreCase(req.email().trim())
        .orElse(null);
    if (u == null || !passwordEncoder.matches(req.password(), u.getPasswordHash())) {
      return ResponseEntity.status(401).build();
    }
    return ResponseEntity.ok(toAuthResponse(u));
  }

  @GetMapping("/me")
  public ResponseEntity<MeResponse> me(org.springframework.security.core.Authentication auth) {
    if (auth == null || auth.getName() == null) return ResponseEntity.status(401).build();
    Long id = tryParseLong(auth.getName());
    if (id == null) return ResponseEntity.status(401).build();

    AuthUser u = repo.findById(id).orElse(null);
    if (u == null) return ResponseEntity.status(404).build();
    return ResponseEntity.ok(new MeResponse(
        String.valueOf(u.getId()),
        u.getEmail(),
        u.getDisplayName(),
        u.getRoles().stream().sorted().toList()
    ));
  }

  private AuthResponse toAuthResponse(AuthUser u) {
    List<String> roles = u.getRoles().stream().sorted().toList();
    String token = jwtService.issueToken(String.valueOf(u.getId()), u.getEmail(), roles);
    return new AuthResponse(
        token,
        "Bearer",
        3600,
        new AuthResponse.UserInfo(String.valueOf(u.getId()), u.getEmail(), u.getDisplayName(), roles)
    );
  }

  private static Long tryParseLong(String v) {
    try {
      return Long.parseLong(v);
    } catch (Exception e) {
      return null;
    }
  }
}


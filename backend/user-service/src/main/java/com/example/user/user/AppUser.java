package com.example.user.user;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class AppUser {

  @Id
  @Column(nullable = false)
  private Long id;

  @Column(nullable = false, length = 320)
  private String email;

  @Column(nullable = false, length = 100)
  private String displayName;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();

  @Column(nullable = false)
  private Instant updatedAt = Instant.now();
}


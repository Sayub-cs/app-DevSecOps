package com.example.business.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "customers",
    indexes = {
        @Index(name = "idx_customers_user_id", columnList = "userId")
    }
)
@Getter
@Setter
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false, length = 120)
  private String name;

  @Column(length = 200)
  private String email;

  @Column(length = 20)
  private String phone;

  @Column(length = 500)
  private String address;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();
}
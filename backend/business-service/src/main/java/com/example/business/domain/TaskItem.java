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
    name = "tasks",
    indexes = {
        @Index(name = "idx_tasks_user_id", columnList = "userId"),
        @Index(name = "idx_tasks_project_id", columnList = "projectId")
    }
)
@Getter
@Setter
public class TaskItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  @Column(nullable = false)
  private Long projectId;

  @Column(nullable = false, length = 160)
  private String title;

  @Column(nullable = false)
  private boolean done = false;

  @Column(nullable = false)
  private Instant createdAt = Instant.now();
}


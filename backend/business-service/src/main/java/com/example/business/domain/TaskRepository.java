package com.example.business.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskItem, Long> {
  List<TaskItem> findAllByUserIdAndProjectIdOrderByIdDesc(Long userId, Long projectId);
}


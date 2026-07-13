package com.example.business.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
  List<Project> findAllByUserIdOrderByIdDesc(Long userId);
}


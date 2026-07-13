package com.example.business.api;

import com.example.business.api.dto.CustomerDto;
import com.example.business.api.dto.ProjectDto;
import com.example.business.api.dto.TaskDto;
import com.example.business.domain.Customer;
import com.example.business.domain.Project;
import com.example.business.domain.TaskItem;

public final class BusinessMapper {
  private BusinessMapper() {}

  public static CustomerDto toDto(Customer c) {
    return new CustomerDto(String.valueOf(c.getId()), c.getName(), c.getEmail(), c.getPhone(), c.getAddress());
  }

  public static ProjectDto toDto(Project p) {
    return new ProjectDto(String.valueOf(p.getId()), p.getName(), p.getDescription());
  }

  public static TaskDto toDto(TaskItem t) {
    return new TaskDto(String.valueOf(t.getId()), String.valueOf(t.getProjectId()), t.getTitle(), t.isDone());
  }
}


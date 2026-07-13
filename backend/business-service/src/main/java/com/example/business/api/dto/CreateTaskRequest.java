package com.example.business.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
    @NotNull String projectId,
    @NotBlank @Size(min = 1, max = 160) String title
) {}


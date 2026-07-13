package com.example.business.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
    @NotBlank @Size(min = 2, max = 120) String name,
    @Size(max = 500) String description
) {}


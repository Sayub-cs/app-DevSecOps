package com.example.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Email @NotBlank @Size(max = 320) String email,
    @NotBlank @Size(min = 2, max = 100) String displayName,
    @NotBlank @Size(min = 8, max = 200) String password
) {}


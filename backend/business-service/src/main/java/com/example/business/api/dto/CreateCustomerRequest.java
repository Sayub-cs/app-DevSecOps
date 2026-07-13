package com.example.business.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
    @NotBlank String name,
    String email,
    String phone,
    String address
) {}
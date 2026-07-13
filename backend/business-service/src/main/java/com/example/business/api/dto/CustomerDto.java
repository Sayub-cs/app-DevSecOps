package com.example.business.api.dto;

public record CustomerDto(
    String id,
    String name,
    String email,
    String phone,
    String address
) {}
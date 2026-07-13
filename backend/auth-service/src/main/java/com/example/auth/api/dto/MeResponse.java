package com.example.auth.api.dto;

import java.util.List;

public record MeResponse(
    String id,
    String email,
    String displayName,
    List<String> roles
) {}


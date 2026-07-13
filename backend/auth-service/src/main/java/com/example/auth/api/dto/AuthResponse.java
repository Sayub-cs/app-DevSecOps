package com.example.auth.api.dto;

import java.util.List;

public record AuthResponse(
    String accessToken,
    String tokenType,
    long expiresInSeconds,
    UserInfo user
) {
  public record UserInfo(String id, String email, String displayName, List<String> roles) {}
}


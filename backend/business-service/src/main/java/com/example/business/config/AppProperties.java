package com.example.business.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Security security, Cors cors) {
  public record Security(Jwt jwt) {
    public record Jwt(String secret, String issuer) {}
  }
  public record Cors(List<String> allowedOrigins) {}
}


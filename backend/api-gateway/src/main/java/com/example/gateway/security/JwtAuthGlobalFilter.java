package com.example.gateway.security;

import java.nio.charset.StandardCharsets;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

  private final JwtVerifier verifier;

  public JwtAuthGlobalFilter(JwtVerifier verifier) {
    this.verifier = verifier;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    if (path.startsWith("/api/auth/") || path.equals("/actuator/health") || path.equals("/actuator/info")) {
      return chain.filter(exchange);
    }

    String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (auth == null || !auth.startsWith("Bearer ")) {
      return unauthorized(exchange);
    }

    String token = auth.substring("Bearer ".length()).trim();
    try {
      Claims claims = verifier.verify(token);
      String userId = claims.getSubject();
      String roles = String.join(",", verifier.roles(claims));

      ServerWebExchange mutated = exchange.mutate()
          .request(r -> r
              .header("X-User-Id", userId == null ? "" : userId)
              .header("X-User-Roles", roles)
          )
          .build();
      return chain.filter(mutated);
    } catch (Exception e) {
      return unauthorized(exchange);
    }
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    byte[] bytes = "{\"error\":\"unauthorized\"}".getBytes(StandardCharsets.UTF_8);
    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
  }

  @Override
  public int getOrder() {
    return -100;
  }
}


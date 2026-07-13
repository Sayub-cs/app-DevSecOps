package com.example.auth.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.auth.config.AppProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final AppProperties props;

  public String issueToken(String userId, String email, List<String> roles) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(props.security().jwt().expirationSeconds());

    return Jwts.builder()
        .issuer(props.security().jwt().issuer())
        .subject(userId)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(Map.of(
            "email", email,
            "roles", roles
        ))
        .signWith(signingKey())
        .compact();
  }

  public Claims parse(String token) {
    return Jwts.parser()
        .verifyWith(signingKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private SecretKey signingKey() {
    byte[] bytes = props.security().jwt().secret().getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(padToMinLength(bytes, 32));
  }

  private static byte[] padToMinLength(byte[] in, int minLen) {
    if (in.length >= minLen) return in;
    byte[] out = new byte[minLen];
    System.arraycopy(in, 0, out, 0, in.length);
    for (int i = in.length; i < minLen; i++) out[i] = (byte) (i * 31);
    return out;
  }
}


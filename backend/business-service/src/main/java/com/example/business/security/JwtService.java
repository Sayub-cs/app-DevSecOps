package com.example.business.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.example.business.config.AppProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

  private final AppProperties props;

  public Claims parse(String token) {
    return Jwts.parser()
        .requireIssuer(props.security().jwt().issuer())
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


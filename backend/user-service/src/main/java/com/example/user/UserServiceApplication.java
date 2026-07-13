package com.example.user;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserServiceApplication {

  public static void main(String[] args) {
    waitForDbIfEnabled();
    SpringApplication.run(UserServiceApplication.class, args);
  }

  private static void waitForDbIfEnabled() {
    boolean enabled = Boolean.parseBoolean(env("STARTUP_DB_WAIT_ENABLED", "true"));
    if (!enabled) return;

    String host = env("DB_HOST", "localhost");
    int port = Integer.parseInt(env("DB_PORT", "5432"));

    long timeoutSeconds = Long.parseLong(env("STARTUP_DB_WAIT_TIMEOUT_SECONDS", "45"));
    long initialDelayMs = Long.parseLong(env("STARTUP_DB_WAIT_INITIAL_DELAY_MS", "300"));

    long deadlineNanos = System.nanoTime() + Duration.ofSeconds(timeoutSeconds).toNanos();
    long delay = Math.max(100, initialDelayMs);

    while (System.nanoTime() < deadlineNanos) {
      if (canConnect(host, port, 1500)) return;
      sleep(delay);
      delay = Math.min((long) (delay * 1.5), 4000);
    }
  }

  private static boolean canConnect(String host, int port, int timeoutMs) {
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(host, port), timeoutMs);
      return true;
    } catch (IOException ignored) {
      return false;
    }
  }

  private static void sleep(long ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private static String env(String key, String def) {
    String v = System.getenv(key);
    return (v == null || v.isBlank()) ? def : v;
  }
}


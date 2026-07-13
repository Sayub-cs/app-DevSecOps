package com.example.auth.api.error;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(this::fieldErr)
        .collect(Collectors.joining(", "));
    return error(HttpStatus.BAD_REQUEST, msg, req);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
    return error(HttpStatus.BAD_REQUEST, "Invalid JSON request body", req);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
    log.error("Unhandled error for {} {}", req.getMethod(), req.getRequestURI(), ex);
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", req);
  }

  private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest req) {
    ApiError body = new ApiError(
        Instant.now(),
        status.value(),
        status.getReasonPhrase(),
        message,
        req.getRequestURI()
    );
    return ResponseEntity.status(status).body(body);
  }

  private String fieldErr(FieldError e) {
    return e.getField() + ": " + (e.getDefaultMessage() == null ? "invalid" : e.getDefaultMessage());
  }
}


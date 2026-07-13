package com.example.business.api.dto;

public record TaskDto(
    String id,
    String projectId,
    String title,
    boolean done
) {}


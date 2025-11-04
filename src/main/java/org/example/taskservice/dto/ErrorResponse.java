package org.example.taskservice.dto;

public record ErrorResponse(String code, String message, String correlationId) {}

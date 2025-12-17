package org.example.taskservice.api.dto;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus httpStatus, String message, String correlationId) {}

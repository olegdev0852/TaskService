package org.example.taskservice.api.dto;

public record ErrorResponse(

       int status,

        String message,

        String correlationId) {}

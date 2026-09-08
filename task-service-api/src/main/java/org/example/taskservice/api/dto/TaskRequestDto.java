package org.example.taskservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskRequestDto(
        @NotBlank @Size(max = 70) String name,
        String description,
        @NotNull Boolean noTest,
        @NotNull Boolean techTask,
        LocalDateTime deadline
) {}
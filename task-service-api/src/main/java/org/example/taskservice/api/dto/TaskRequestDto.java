package org.example.taskservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequestDto(
        @NotBlank @Size(max = 70) String name,
         String description
) {}

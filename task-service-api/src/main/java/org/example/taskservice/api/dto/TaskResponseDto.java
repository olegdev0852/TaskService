package org.example.taskservice.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponseDto(
        long id,
        String name,
        String description,
        Boolean techTask,
        Boolean noTest,
        UUID assignedTo,
        LocalDateTime deadline,
        LocalDateTime timeOfCreation,
        boolean completed
){}


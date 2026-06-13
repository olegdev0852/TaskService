package org.example.taskservice.api.dto;

import org.example.taskservice.api.state.TaskState;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponseDto(
        long id,
        String name,
        String description,
        TaskState state,
        boolean techTask,
        boolean noTest,
        UUID assignedTo,
        LocalDateTime deadline,
        LocalDateTime createdAt
){}


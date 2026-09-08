package org.example.taskservice.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignTaskRequest(
        UUID assignedTo,
        LocalDateTime deadline
) {}

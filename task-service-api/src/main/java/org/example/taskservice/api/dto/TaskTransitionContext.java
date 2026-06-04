package org.example.taskservice.api.dto;

import org.example.taskservice.api.state.TaskState;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskTransitionContext(
        Long taskId,
        TaskState currentState,
        UUID assignedTo,
        LocalDateTime deadline,
        boolean noTest,
        boolean techTask
) {}
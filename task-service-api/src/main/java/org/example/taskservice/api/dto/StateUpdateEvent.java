package org.example.taskservice.api.dto;

import org.example.taskservice.api.state.TaskState;

public record StateUpdateEvent(
            Long taskId,
            TaskState newState
) {}

package org.example.taskservice.event;

import org.example.taskservice.api.state.TaskState;

public record TaskStateUpdatedEvent(Long taskId, TaskState newState) {}
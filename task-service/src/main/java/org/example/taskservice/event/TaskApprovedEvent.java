package org.example.taskservice.event;

import org.example.taskservice.entity.Task;

public record TaskApprovedEvent(Task task) {}


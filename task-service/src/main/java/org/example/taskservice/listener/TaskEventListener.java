package org.example.taskservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.event.TaskApprovedEvent;
import org.example.taskservice.event.TaskAssignedEvent;
import org.example.taskservice.event.TaskCreatedEvent;
import org.example.taskservice.kafka.TaskEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventListener {

    private final TaskEventPublisher kafkaEventPublisher;

    @Async
    @EventListener
    public void onTaskCreated(TaskCreatedEvent event) {
        log.info("Получено внутреннее событие: задача {} создана. Отправляю в Kafka...", event.task().getId());
        kafkaEventPublisher.publishTransition(event.task(), "TASK_CREATED");
    }

    @Async
    @EventListener
    public void onTaskAssigned(TaskAssignedEvent event) {
        log.info("Получено внутреннее событие: задаче {} назначен исполнитель. Отправляю в Kafka...", event.task().getId());
        kafkaEventPublisher.publishTransition(event.task(), "EXECUTOR_ASSIGNED");
    }

    @Async
    @EventListener
    public void onTaskApproved(TaskApprovedEvent event) {
        log.info("Получено внутреннее событие: задача {} аппрувнута. Отправляю в Kafka...", event.task().getId());
        kafkaEventPublisher.publishTransition(event.task(), "STEP_APPROVED");
    }
}
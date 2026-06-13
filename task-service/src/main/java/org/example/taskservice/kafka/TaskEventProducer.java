package org.example.taskservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskservice.api.dto.TaskTransitionContext;
import org.example.taskservice.entity.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEventProducer {

    @Value("${kafka.task-service.output-topic}")
    private String outputTopic;

    private final KafkaTemplate<String, TaskTransitionContext> kafkaTemplate;

    public void publishTransition(Task task) {
        TaskTransitionContext event = new TaskTransitionContext(
                task.getId(),
                task.getState(),
                task.getAssignedTo(),
                task.getDeadline(),
                task.isNoTest(),
                task.isTechTask()
        );

        log.info("📤 Отправка события в {}: taskId={}, state={}",
                outputTopic, task.getId(), task.getState());

        kafkaTemplate.send(outputTopic, task.getId().toString(), event);
    }
}
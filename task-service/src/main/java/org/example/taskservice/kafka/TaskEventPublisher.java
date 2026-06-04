package org.example.taskservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.taskservice.api.dto.TaskTransitionContext;
import org.example.taskservice.entity.Task;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEventPublisher {

    private static final String TOPIC = "workflow.input";

    private final KafkaTemplate<String, TaskTransitionContext> kafkaTemplate;

    public void publishTransition(Task task, String triggerName) {
        TaskTransitionContext event = new TaskTransitionContext(
                task.getId(),
                task.getState(),
                task.getAssignedTo(),
                task.getDeadline(),
                task.isNoTest(),
                task.isTechTask()
        );

        ProducerRecord<String, TaskTransitionContext> record =
                new ProducerRecord<>(TOPIC, task.getId().toString(), event);

        record.headers()
                .add("ce-specversion", "1.0".getBytes(StandardCharsets.UTF_8))
                .add("ce-type", "task.state.transition.request".getBytes(StandardCharsets.UTF_8))
                .add("ce-source", "/task-service".getBytes(StandardCharsets.UTF_8))
                .add("ce-id", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
                .add("ce-time", Instant.now().toString().getBytes(StandardCharsets.UTF_8))
                .add("ce-subject", ("task-" + task.getId()).getBytes(StandardCharsets.UTF_8))

                .add("trigger", triggerName.getBytes(StandardCharsets.UTF_8))
                .add("schema-version", "1.0".getBytes(StandardCharsets.UTF_8));

        log.info("📤 Отправка события в {}: taskId={}, trigger={}, state={}",
                TOPIC, task.getId(), triggerName, task.getState());

        kafkaTemplate.send(record).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("✅ Событие отправлено: taskId={}, partition={}, offset={}",
                        task.getId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("❌ Ошибка отправки события для задачи {}", task.getId(), ex);
            }
        });
    }
}
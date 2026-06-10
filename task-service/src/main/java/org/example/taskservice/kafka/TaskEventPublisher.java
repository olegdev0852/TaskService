package org.example.taskservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.taskservice.api.dto.TaskTransitionContext;
import org.example.taskservice.entity.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskEventPublisher {

    @Value("${kafka.task-service.output-topic}")
    private String OUTPUT_TOPIC;

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
                new ProducerRecord<>(OUTPUT_TOPIC, task.getId().toString(), event);

        record.headers().add("trigger", triggerName.getBytes(StandardCharsets.UTF_8));

        log.info("📤 Отправка события в {}: taskId={}, trigger={}, state={}",
                OUTPUT_TOPIC, task.getId(), triggerName, task.getState());

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
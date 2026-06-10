package org.example.taskservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.example.taskservice.api.dto.StateUpdateEvent;
import org.example.taskservice.entity.Task;
import org.example.taskservice.repository.TaskRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStateUpdateListener {

    private final TaskRepository taskRepository;

    @KafkaListener(
            topics = "${kafka.task-service.input-topic}",
            groupId = "${kafka.task-service.group-id:task-service-group}",
            containerFactory = "stateUpdateKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleStateUpdate(ConsumerRecord<String, StateUpdateEvent> record) {
        StateUpdateEvent event = record.value();

        Header correlationHeader = record.headers().lastHeader("correlation-id");
        String correlationId = correlationHeader != null
                ? new String(correlationHeader.value(), StandardCharsets.UTF_8)
                : "unknown";

        log.info("📥 Получено обновление состояния: taskId={}, newState={}, correlationId={}",
                event.taskId(), event.newState(), correlationId);

        Task task = taskRepository.findById(event.taskId()).orElse(null);

        if(task ==null){
            log.warn("Задача {} не найдена в БД. Сообщение пропущено", event.taskId());
            return;
        }

        task.setState(event.newState());
        taskRepository.save(task);

        log.info("✅ Состояние задачи {} обновлено на {} (correlationId: {})",
                task.getId(), event.newState(), correlationId);
    }
}
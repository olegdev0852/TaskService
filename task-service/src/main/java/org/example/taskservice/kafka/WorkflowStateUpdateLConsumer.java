package org.example.taskservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.taskservice.api.dto.StateUpdateEvent;
import org.example.taskservice.event.TaskStateUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowStateUpdateLConsumer {

   private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(
            topics = "${kafka.task-service.input-topic}",
            groupId = "${kafka.task-service.group-id:task-service-group}",
            containerFactory = "stateUpdateKafkaListenerContainerFactory"
    )
    @Transactional
    public void handleStateUpdate(ConsumerRecord<String, StateUpdateEvent> record) {
        StateUpdateEvent event = record.value();

        log.info("Получено из kafka: taskId={}, newState={}",
                event.taskId(), event.newState());

      eventPublisher.publishEvent(new TaskStateUpdatedEvent(event.taskId(), event.newState()));
    }
}
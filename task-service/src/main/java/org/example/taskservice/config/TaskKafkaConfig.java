package org.example.taskservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.example.taskservice.api.dto.StateUpdateEvent;
import org.example.taskservice.api.dto.TaskTransitionContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TaskKafkaConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<String, TaskTransitionContext> taskTransitionProducerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaProperties.getProducer().getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProperties.getProducer().getBatchSize());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaProperties.getProducer().getBufferMemory());

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                kafkaProperties.getProducer().getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                kafkaProperties.getProducer().getValueSerializer());

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, TaskTransitionContext> taskTransitionKafkaTemplate() {
        return new KafkaTemplate<>(taskTransitionProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, StateUpdateEvent> stateUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getTaskService().getGroupId());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                kafkaProperties.getConsumer().getAutoOffsetReset());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                kafkaProperties.getConsumer().isEnableAutoCommit());

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                kafkaProperties.getConsumer().getKeyDeserializer());

        JsonDeserializer<StateUpdateEvent> jsonDeserializer =
                new JsonDeserializer<>(StateUpdateEvent.class, false);

        String trustedPackages = kafkaProperties.getConsumer()
                .getProperties().getSpringJsonTrustedPackages();
        if (trustedPackages != null && !trustedPackages.isEmpty()) {
            jsonDeserializer.addTrustedPackages(trustedPackages.split(","));
        }

        ErrorHandlingDeserializer<StateUpdateEvent> errorHandlingDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new org.apache.kafka.common.serialization.StringDeserializer(),
                errorHandlingDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StateUpdateEvent>
    stateUpdateKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StateUpdateEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(stateUpdateConsumerFactory());
        factory.setConcurrency(kafkaProperties.getTaskService().getConcurrency());
        return factory;
    }
}
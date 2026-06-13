package org.example.taskservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private ProducerProperties producer = new ProducerProperties();
    private ConsumerProperties consumer = new ConsumerProperties();
    private TaskServiceProperties taskService = new TaskServiceProperties();

    @Data
    public static class ProducerProperties {
        private String acks = "all";
        private int retries = 3;
        private int batchSize = 16384;
        private long bufferMemory = 33554432L;
    }

    @Data
    public static class ConsumerProperties {
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = false;
    }

    @Data
    public static class TaskServiceProperties {
        private String groupId = "task-service-group";
        private int concurrency = 1;
        private String inputTopic = "workflow.output";
        private String outputTopic = "workflow.input";
    }
}
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

    /**
    тут хоть и прописаны свойства кафки но они все равно подтягиваются из yaml,
     а если вдруг в yaml нет определенного поля то берутся от сюда
     Возможно это кринж не уверен
     */
    @Data
    public static class ProducerProperties {
        private String acks = "all";
        private int retries = 3;
        private int batchSize = 16384;
        private long bufferMemory = 33554432L;
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "org.springframework.kafka.support.serializer.JsonSerializer";
    }

    @Data
    public static class ConsumerProperties {
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = false;
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.springframework.kafka.support.serializer.JsonDeserializer";
        private Properties properties = new Properties();

        @Data
        public static class Properties {
            private String springJsonTrustedPackages;
            private String springJsonValueDefaultType;
        }
    }

    @Data
    public static class TaskServiceProperties {
        private String groupId = "task-service-group";
        private int concurrency = 1;
    }
}
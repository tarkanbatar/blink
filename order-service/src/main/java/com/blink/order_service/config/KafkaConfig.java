package com.blink.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** Automatically creates Kafka topics */
@Configuration
public class KafkaConfig {
    
    @Value("${kafka.topic.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topic.order-updated}")
    private String orderUpdatedTopic;

    @Value("${kafka.topic.order-deleted}")
    private String orderDeletedTopic;

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(orderCreatedTopic)
                            .partitions(3)
                            .replicas(1)
                            .build();
    }

    @Bean
    public NewTopic orderUpdateTopic() {
        return TopicBuilder.name(orderUpdatedTopic)
                            .partitions(3)
                            .replicas(1)
                            .build();
    }

    @Bean
    public NewTopic orderDeletedTopic() {
        return TopicBuilder.name(orderDeletedTopic)
                            .partitions(3)
                            .replicas(1)
                            .build();
    }
}

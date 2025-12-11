package com.blink.notification_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.blink.notification_service.dto.OrderEvent;
import com.blink.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka Order Event Consumer
 * It listens to order events from Kafka and processes them accordingly.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    
    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topics.order-created}",groupId = "${spring.kafka.consumer.group-id}",containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderCreated(@Payload OrderEvent event, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition, @Header(KafkaHeaders. OFFSET) long offset) {
        log.info("Received ORDER_CREATED event. Topic: {} , Partition: {} , Offset: {} , Order: {} ", topic, partition, offset, event.getOrderNumber());
    
        try{
            notificationService.processOrderEvent(event);
        } catch (Exception e) {
            log.error("Error processing ORDER_CREATED event for Order: {}. Error: {}", event.getOrderNumber(), e.getMessage()); 
            //! Buraya DLQ veya retry eklenecek
        }
    }


    @KafkaListener(topics = "${kafka. topics.order-updated}",groupId = "${spring.kafka.consumer.group-id}",containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderUpdated(@Payload OrderEvent event,@Header(KafkaHeaders. RECEIVED_TOPIC) String topic,@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,@Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received ORDER_UPDATED event. Topic: {}, Partition: {}, Offset: {}, Order: {}, Status: {}", topic, partition, offset, event.getOrderNumber(), event.getEventType());

        try {
            notificationService.processOrderEvent(event);
        } catch (Exception e) {
            log.error("Error processing ORDER_UPDATED event for order {}: {}", event.getOrderNumber(), e.getMessage(), e);
        }
    }


    @KafkaListener(topics = "${kafka.topics.order-cancelled}",groupId = "${spring.kafka.consumer.group-id}",containerFactory = "kafkaListenerContainerFactory")
    public void handleOrderCancelled(@Payload OrderEvent event,@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,@Header(KafkaHeaders.RECEIVED_PARTITION) int partition,@Header(KafkaHeaders.OFFSET) long offset) {
        log.info("Received ORDER_CANCELLED event. Topic: {}, Partition: {}, Offset: {}, Order: {}", topic, partition, offset, event.getOrderNumber());

        try {
            notificationService. processOrderEvent(event);
        } catch (Exception e) {
            log.error("Error processing ORDER_CANCELLED event for order {}:  {}", event.getOrderNumber(), e.getMessage(), e);
        }
    }
}

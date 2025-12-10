package com.blink.order_service.event;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.blink.order_service.model.EventType;
import com.blink.order_service.model.Order;
import com.blink.order_service.model.OrderEvent;
import com.blink.order_service.model.OrderItemEvent;
import com.blink.order_service.model.OrderStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for publishing order-related events to Kafka
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {
    
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${kafka.topic.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topic.order-updated}")
    private String orderUpdatedTopic;

    @Value("${kafka.topic.order-cancelled}")
    private String orderCancelledTopic;

    /* Order Created Event */
    public void publishOrderCreated(Order order) {
        OrderEvent orderEvent = buildEvent(order, EventType.ORDER_CREATED);
        publish(orderCreatedTopic, order.getId(), orderEvent);
    }

    /* Order Status Updated Event */
    public void publishOrderStatusUpdated(Order order) {
         EventType eventType = mapOrderStatusToEventType(order.getStatus());
         OrderEvent event = buildEvent(order, eventType);

         String topic = order.getStatus() == OrderStatus.CANCELLED ? orderCancelledTopic : orderUpdatedTopic;
         publish(topic, order.getId(), event);
    }

    /* CREATE EVENT */
    private OrderEvent buildEvent (Order order, EventType eventType) {
        return OrderEvent.builder()
                .eventType(eventType)
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUserId())
                .userEmail(order.getUserEmail())
                .orderStatus(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .totalItems(order.getTotalItems())
                .items(order.getItems().stream().map(item -> 
                    OrderItemEvent.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .build()
                ).toList())
                .trackingNumber(order.getTrackingNumber())
                .cancellationReason(order.getCancellationReason())
                .estimatedDeliveryDate(order.getEstimatedDeliveryDate())
                .build();
    }

    /**
     * OrderStatus -> EventType mapping
     */
    private EventType mapOrderStatusToEventType(OrderStatus status) {
        return switch (status) {
            case PENDING -> EventType.ORDER_CREATED;
            case CONFIRMED -> EventType.ORDER_CONFIRMED;
            case PROCESSING -> EventType.ORDER_PROCESSING;
            case SHIPPED -> EventType.ORDER_SHIPPED;
            case DELIVERED -> EventType.ORDER_DELIVERED;
            case CANCELLED ->  EventType.ORDER_CANCELLED;
            case REFUNDED -> EventType.ORDER_REFUNDED;
        };
    }

    /* PUBLISH EVENT TO TOPIC */
    private void publish (String topic, String key, OrderEvent event) {
        log.info("Publishing event to topic: {} Key: {} EventType: {}", topic, key, event.getEventType());

        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if(ex == null) 
                log.info("Event published successfully to topic: {} Partition: {} Offset: {}", topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            else
                log.error("Failed to publish event to topic: {} Key: {} Error: {}", topic, key, ex.getMessage());
        });
    }
}

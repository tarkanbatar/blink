package com.blink.notification_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.blink.notification_service.model.EventType;
import com.blink.notification_service.model.OrderItemEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order event for the event that will be consumed from Kafka topic.
 * It should match with the OrderEvent class in order-service.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private EventType eventType;
    private String orderId;
    private String orderNumber;
    private String userId;
    private String userEmail;
    private String status;
    private BigDecimal totalAmount;
    private int totalItems;
    private List<OrderItemEvent> items;
    private String trackingNumber;
    private String cancellationReason;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime timestamp;
}

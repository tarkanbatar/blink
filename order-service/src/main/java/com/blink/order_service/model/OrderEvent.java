package com.blink.order_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event class that we'll send it to Kafka topic.
 * Notification service will listen to this topic and send email/SMS notifications to users.
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
    private OrderStatus orderStatus;
    private BigDecimal totalAmount;
    private int totalItems;
    private List<OrderItemEvent> items;

    private String trackingNumber;
    private String cancellationReason;
    private LocalDateTime estimatedDeliveryDate;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}

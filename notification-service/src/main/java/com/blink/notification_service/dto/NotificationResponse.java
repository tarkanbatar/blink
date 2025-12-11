package com.blink.notification_service.dto;

import java.time.LocalDateTime;

import com.blink.notification_service.model.Notification;
import com.blink.notification_service.model.NotificationChannel;
import com.blink.notification_service.model.NotificationStatus;
import com.blink.notification_service.model.NotificationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    
    private String id;
    private String userId;
    private NotificationType type;
    private String typeDescription;
    private NotificationChannel channel;
    private NotificationStatus status;
    private String title;
    private String content;
    private String orderId;
    private String orderNumber;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification. getType())
                .typeDescription(notification.getType().getDescription())
                .channel(notification. getChannel())
                .status(notification.getStatus())
                .title(notification.getTitle())
                .content(notification.getContent())
                .orderId(notification.getOrderId())
                .orderNumber(notification.getOrderNumber())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

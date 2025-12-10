package com.blink.notification_service.model;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    // receiver user id
    @Indexed
    private String userId;

    private String email;
    
    @Indexed
    private NotificationType type;

    private NotificationChannel channel;

    @Indexed
    @Builder.Default
    private NotificationStatus status = NotificationStatus.PENDING;

    private String title;
    private String content;

    @Indexed
    private String orderId;

    private String orderNumber;

    // additional data like template variables
    private Map<String, Object> metadata;

    // if status is FAILED, store error message
    private String errorMessage;

    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    @Builder.Default
    private Integer retryCount = 0;

    @CreatedDate
    private LocalDateTime createdAt;
    
}

package com.blink.notification_service.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blink.notification_service.dto.NotificationResponse;
import com.blink.notification_service.dto.OrderEvent;
import com.blink.notification_service.model.EventType;
import com.blink.notification_service.model.Notification;
import com.blink.notification_service.model.NotificationChannel;
import com.blink.notification_service.model.NotificationStatus;
import com.blink.notification_service.model.NotificationType;
import com.blink.notification_service.repository.NotificationRepository;
import com.blink.notification_service.service.EmailService;
import com.blink.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Override
    public void processOrderEvent(OrderEvent event) {
        log.info("Processing order event: {} for order: {}", event.getEventType(), event.getOrderNumber());

        NotificationType type = mapEventTypeToNotificationType(event.getEventType());
        Notification notification = createNotification(event, type);

        try {
            sendEmailByEventType(event);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

            log.info("Notification sent successfully for order: {}", event.getOrderNumber());
        } catch (Exception e) {
            log.error("Failed to send notification email for order: {}. Error: {}", event.getOrderNumber(), e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
        }
        notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(String userId, int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable).getContent().stream().map(NotificationResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndStatusNot(userId, NotificationStatus.READ);
    }

    @Override
    public NotificationResponse markAsRead(String notificationId) {
        
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        log.info("Notification read: {}", notificationId);
        return NotificationResponse.fromEntity(saved);
    }

    @Override
    public void markAllAsRead(String userId) {
        
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndStatusNotOrderByCreatedAtDesc(userId, NotificationStatus.READ);
        LocalDateTime now = LocalDateTime.now();

        unreadNotifications.forEach(notification -> {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(unreadNotifications);
        log.info("All notifications marked as read for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));
        return NotificationResponse.fromEntity(notification);
    }



    /* HELPERS */
    

    private Notification createNotification(OrderEvent event, NotificationType type) {
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("totalAmount", event.getTotalAmount());
        metadata.put("totalItems", event.getTotalItems());
        metadata.put("items", event.getItems());

        if (event.getTrackingNumber() != null) {
            metadata.put("trackingNumber", event.getTrackingNumber());
        }
        if (event.getEstimatedDeliveryDate() != null) {
            metadata.put("estimatedDeliveryDate", event.getEstimatedDeliveryDate());
        }
        if (event.getCancellationReason() != null) {
            metadata.put("cancellationReason", event.getCancellationReason());
        }

        return Notification.builder()
                .userId(event.getUserId())
                .email(event.getUserEmail())
                .type(type)
                .channel(NotificationChannel.EMAIL)
                .title(generateTitle(type, event.getOrderNumber()))
                .content(generateContent(type, event))
                .orderId(event.getOrderId())
                .orderNumber(event.getOrderNumber())
                .metadata(metadata)
                .build();
    }

    private void sendEmailByEventType(OrderEvent event) {
        switch (event.getEventType()) {
            case ORDER_CREATED -> emailService.sendOrderCreatedEmail(event);
            case ORDER_CONFIRMED -> emailService.sendOrderConfirmedEmail(event);
            case ORDER_SHIPPED -> emailService.sendOrderShippedEmail(event);
            case ORDER_DELIVERED -> emailService.sendOrderDeliveredEmail(event);
            case ORDER_CANCELLED -> emailService.sendOrderCancelledEmail(event);
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private NotificationType mapEventTypeToNotificationType(EventType eventType) {
        return switch (eventType) {
            case ORDER_CREATED -> NotificationType.ORDER_CREATED;
            case ORDER_CONFIRMED -> NotificationType. ORDER_CONFIRMED;
            case ORDER_PROCESSING -> NotificationType.ORDER_PROCESSING;
            case ORDER_SHIPPED -> NotificationType.ORDER_SHIPPED;
            case ORDER_DELIVERED -> NotificationType.ORDER_DELIVERED;
            case ORDER_CANCELLED -> NotificationType. ORDER_CANCELLED;
            case ORDER_REFUNDED -> NotificationType.ORDER_REFUNDED;
        };
    }

    private String generateTitle(NotificationType type, String orderNumber) {
        return String.format("%s - %s", type.getDescription(), orderNumber);
    }

    private String generateContent(NotificationType type, OrderEvent event) {
        return String.format(
                "%s.  Sipariş No: %s, Toplam:  %. 2f TL",
                type.getDescription(),
                event. getOrderNumber(),
                event. getTotalAmount()
        );
    }
}

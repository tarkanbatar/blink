package com.blink.notification_service.service;

import java.util.List;

import com.blink.notification_service.dto.NotificationResponse;
import com.blink.notification_service.dto.OrderEvent;

public interface NotificationService {
    void processOrderEvent(OrderEvent event);
    List<NotificationResponse> getUserNotifications(String userId, int page, int size);
    long getUnreadCount(String userId);
    NotificationResponse markAsRead(String notificationId);
    void markAllAsRead(String userId);
    NotificationResponse getNotificationById(String notificationId);
}

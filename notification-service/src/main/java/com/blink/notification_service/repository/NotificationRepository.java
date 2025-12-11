package com.blink.notification_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.blink.notification_service.model.Notification;
import com.blink.notification_service.model.NotificationStatus;
import com.blink.notification_service.model.NotificationType;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    List<Notification> findByUserIdAndStatusNotOrderByCreatedAtDesc(String userId, NotificationStatus status);
    long countByUserIdAndStatusNot(String userId, NotificationStatus status);
    Page<Notification> findByType(NotificationType type, Pageable pageable);
    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, int maxRetryCount);
    List<Notification> findByOrderId(String orderId);
}

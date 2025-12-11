package com.blink.notification_service.service;

import com.blink.notification_service.dto.OrderEvent;

public interface EmailService {
    void sendOrderCreatedEmail(OrderEvent event);
    void sendOrderConfirmedEmail(OrderEvent event);
    void sendOrderShippedEmail(OrderEvent event);
    void sendOrderDeliveredEmail(OrderEvent event);
    void sendOrderCancelledEmail(OrderEvent event);
    void sendEmail(String to, String subject, String htmlContent);
}

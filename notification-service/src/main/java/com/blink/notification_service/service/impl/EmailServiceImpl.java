package com.blink.notification_service.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.blink.notification_service.dto.OrderEvent;
import com.blink.notification_service.service.EmailService;
import com.blink.notification_service.template.EmailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${spring.mail.test-mode: true}")
    private boolean testMode;

    @Override
    @Async // it should work as async to not block the main thread
    public void sendOrderCreatedEmail(OrderEvent event) {
        String subject = String.format("Your order %s has been created!", event.getOrderNumber());
        String content = templateService.generateOrderCreatedEmail(event);
        sendEmail(event.getUserEmail(), subject, content);
    }

    @Override
    @Async
    public void sendOrderConfirmedEmail(OrderEvent event) {
        String subject = String.format("Your order %s is confirmed!", event.getOrderId());
        String content = templateService.generateOrderConfirmedEmail(event);
        sendEmail(event.getUserEmail(), subject, content);
    }

    @Override
    @Async
    public void sendOrderShippedEmail(OrderEvent event) {
        String subject = String.format("Your order %s has been shipped!", event.getOrderNumber());
        String content = templateService.generateOrderShippedEmail(event);
        sendEmail(event.getUserEmail(), subject, content);
    }

    @Override
    @Async
    public void sendOrderDeliveredEmail(OrderEvent event) {
        String subject = String.format("Your order %s has been delivered!", event.getOrderNumber());
        String content = templateService.generateOrderDeliveredEmail(event);
        sendEmail(event.getUserEmail(), subject, content);
    }

    @Override
    @Async
    public void sendOrderCancelledEmail(OrderEvent event) {
        String subject = String.format("Your order %s has been cancelled!", event.getOrderNumber());
        String content = templateService.generateOrderCancelledEmail(event);
        sendEmail(event.getUserEmail(), subject, content);
    }

    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        // Test modunda gerçek email gönderme
        if (testMode) {
            log.info("========== TEST MODE - EMAIL ===========");
            log.info("To: {}", to);
            log.info("Subject: {}", subject);
            log.info("Content Preview: {}", htmlContent.substring(0, Math.min(200, htmlContent.length())));
            log.info("=========================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper. setText(htmlContent, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Email gönderilemedi", e);
        }
    }
}

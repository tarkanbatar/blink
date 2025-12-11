package com.blink.notification_service.model;

public enum NotificationType {
    ORDER_CREATED("Order Created"),
    ORDER_CONFIRMED("Order Confirmed"),
    ORDER_PROCESSING("Order Processing"),
    ORDER_SHIPPED("Order Shipped"),
    ORDER_DELIVERED("Order Delivered"),
    ORDER_CANCELLED("Order Cancelled"),
    ORDER_REFUNDED("Order Refunded"),
    WELCOME("Welcome"),
    PASSWORD_RESET("Password Reset"),
    PROMOTIONAL("Promotional");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

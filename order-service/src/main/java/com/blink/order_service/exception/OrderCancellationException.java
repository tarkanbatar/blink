package com.blink.order_service.exception;

public class OrderCancellationException extends RuntimeException {

    public OrderCancellationException(String orderId, String reason) {
        super(String.format("Order with ID %s cannot be cancelled. Reason: %s", orderId, reason));
    }
    
}

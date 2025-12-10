package com.blink.order_service.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException(String userId) {
        super(String.format("The cart is empty. Cannot place an order with no items. User ID: %s", userId));
    }
    
}

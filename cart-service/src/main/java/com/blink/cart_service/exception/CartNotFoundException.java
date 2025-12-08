package com.blink.cart_service.exception;

public class CartNotFoundException extends RuntimeException {
      
    public CartNotFoundException(String userId) {
        super("Cart not found for user with ID: " + userId);
    }
}

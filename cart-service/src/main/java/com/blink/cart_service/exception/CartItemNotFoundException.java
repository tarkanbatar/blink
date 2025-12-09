package com.blink.cart_service.exception;

public class CartItemNotFoundException extends RuntimeException {
    
    public CartItemNotFoundException(String productId) {
        super("Cart item not found with ID: " + productId);
    }
}

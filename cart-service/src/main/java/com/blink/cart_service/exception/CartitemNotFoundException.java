package com.blink.cart_service.exception;

public class CartitemNotFoundException extends RuntimeException {
    
    public CartitemNotFoundException(String productId) {
        super("Cart item not found with ID: " + productId);
    }
}

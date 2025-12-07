package com.blink.product_service.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String field, String value) {
        super(String.format("Product not found with field: %s and value: %s", field, value));
    }
}

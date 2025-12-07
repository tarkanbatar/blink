package com.blink.product_service.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(String field, String value) {
        super(String.format("Category not found with field: %s and value: %s", field, value));
    }
    
}

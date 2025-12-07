package com.blink.product_service.exception;

public class SkuAlreadyExistsException extends RuntimeException {
    public SkuAlreadyExistsException(String sku) {
        super(String.format("SKU already exists: %s", sku));
    }
    
}

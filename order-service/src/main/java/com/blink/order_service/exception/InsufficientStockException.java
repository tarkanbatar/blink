package com.blink.order_service.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int requestedQuantity, int availableQuantity) {
        super(String.format("Insufficient stock for product ID %s. Requested: %d, Available: %d", productName, requestedQuantity, availableQuantity));
    }
    
}

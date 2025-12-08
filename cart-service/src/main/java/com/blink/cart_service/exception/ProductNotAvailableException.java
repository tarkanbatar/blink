package com.blink.cart_service.exception;


public class ProductNotAvailableException extends RuntimeException{

    public ProductNotAvailableException(String productId) {
        super(String.format("Product with ID %s is not available in the requested quantity.", productId));
    }

    public ProductNotAvailableException(String productId, String reason) {
        super(String.format("Ürün sepete eklenemedi: %s. Sebep: %s", productId, reason));
    }
}

package com.blink.order_service.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String identifier){
        super(String.format("Order with identifier %s not found", identifier));
    }
    
}

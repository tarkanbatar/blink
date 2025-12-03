package com.blink.user_service.exception;

public class AddressNotFoundException extends RuntimeException {
    
    public AddressNotFoundException(String addressId) {
        super(String.format("Address not found: %s", addressId));
    }
    
}

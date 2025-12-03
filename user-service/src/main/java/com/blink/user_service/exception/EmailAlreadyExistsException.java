package com.blink.user_service.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email already exists: %s", email));
    }    
}

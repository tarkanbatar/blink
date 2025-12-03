package com.blink.user_service.exception;

// using runtime exception for unchecked exception to provide flexibility and clean code
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String field, String value){
        super(String.format("User not found: %s = %s", field, value));
    }
}
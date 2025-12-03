package com.blink.user_service.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

// Standard error response class for API exceptions. It may provide easy parsing to error details on client side.

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class ErrorResponse {
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;   // HTTP status code
    private String error;  // Short description of the error
    private String message; // Detailed error message
    private String path;    // Request path that caused the error

    private Map<String, String> validationErrors; // For validation errors, field-specific messages
}

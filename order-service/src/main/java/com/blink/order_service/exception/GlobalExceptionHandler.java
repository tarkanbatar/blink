package com.blink.order_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        log.error("Order not found exception: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.NOT_FOUND.value())
                                        .error("Not Found")
                                        .message(ex.getMessage())
                                        .path(request.getRequestURI())
                                        .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleOrderCancellation(OrderCancellationException ex, HttpServletRequest request) {
        log.warn("Order cancellation failed: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.BAD_REQUEST.value())
                                        .error("Bad Request")
                                        .message(ex.getMessage())
                                        .path(request.getRequestURI())
                                        .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        log.warn("Insufficient stock: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.CONFLICT.value())
                                        .error("Conflict")
                                        .message(ex.getMessage())
                                        .path(request.getRequestURI())
                                        .build();
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors (MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation errors: {}", errors);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message("Validation Error")
                .path(request.getRequestURI())
                .validationErrors(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected exception occured: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                        .error("Internal Server Error")
                                        .message("An unexpected error occurred.")
                                        .path(request.getRequestURI())
                                        .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
}

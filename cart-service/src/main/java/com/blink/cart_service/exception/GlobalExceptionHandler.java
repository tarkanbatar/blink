package com.blink.cart_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartNotFound(CartNotFoundException ex, HttpServletRequest request) {
        log.error("Cart not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.SC_NOT_FOUND)
                                        .error("Not Found")
                                        .message(ex.getMessage())
                                        .path(request.getRequestURI())
                                        .build();
        
        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(error);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex, HttpServletRequest request) {
        log.error("Cart item not found: {}", ex. getMessage());

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.SC_NOT_FOUND)
                                        .error("Not Found")
                                        .message(ex.getMessage())
                                        .path(request.getRequestURI())
                                        .build();

        return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(error);
    }


    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleProductNotAvailable( ProductNotAvailableException ex, HttpServletRequest request) {
        log.warn("Product not available: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.SC_BAD_REQUEST)
                                        .error("Bad Request")
                                        .message(ex.getMessage())
                                        .path(request.getRequestURI())
                                        .build();

        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors( MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.SC_BAD_REQUEST)
                                        .error("Bad Request")
                                        .message("Validation hatası")
                                        .path(request.getRequestURI())
                                        .validationErrors(errors)
                                        .build();

        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception. class)
    public ResponseEntity<ErrorResponse> handleGenericException( Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex);

        ErrorResponse error = ErrorResponse.builder()
                                        .status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                                        .error("Internal Server Error")
                                        .message("Beklenmeyen bir hata oluştu")
                                        .path(request.getRequestURI())
                                        .build();

        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(error);
    }
}

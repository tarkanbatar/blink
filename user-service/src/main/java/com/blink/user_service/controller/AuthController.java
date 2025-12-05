package com.blink.user_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.blink.user_service.dto.request.LoginRequest;
import com.blink.user_service.dto.request.RegisterRequest;
import com.blink.user_service.dto.response.AuthResponse;
import com.blink.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final UserService userService;

    /**
    * POST /api/auth/register 
    * Handles user registration requests.
    * It runs the validation annotations in RegisterRequest DTO.
    * If validation fails, it throws MethodArgumentNotValidException before reaching the service layer and cathches it in GlobalExceptionHandler.
    */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());
        AuthResponse response = userService.register(request);
        log.info("User registered successfully with email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        AuthResponse response = userService.login(request);
        log.info("User logged in successfully with email: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
    
    
}

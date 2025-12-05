package com.blink.user_service.service;

import java.util.List;

import com.blink.user_service.dto.request.AddressRequest;
import com.blink.user_service.dto.request.LoginRequest;
import com.blink.user_service.dto.request.RegisterRequest;
import com.blink.user_service.dto.request.UpdateProfileRequest;
import com.blink.user_service.dto.response.AuthResponse;
import com.blink.user_service.dto.response.UserResponse;
import com.blink.user_service.model.User;

/**
 * Reasons of using interface here:
 * 1. Loose Coupling: By defining UserService as an interface, we decouple the service definition from its implementation. 
 *    This allows for easier swapping of implementations without affecting the rest of the codebase.
 * 2. Testability: Interfaces facilitate mocking and stubbing in unit tests. We can easily create mock implementations of UserService for testing purposes.
 * 3. Flexibility: It allows to implement different parts like cache, transaction managements etc.
 * 4. SOLID: Dependency Inversion Principle adherence, which states that high-level modules should not depend on low-level modules but on abstractions.
 */

public interface UserService {

    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(String email);
    UserResponse getUserById(String id);
    List<UserResponse> getAllUsers();
    UserResponse updateProfile(String email, UpdateProfileRequest request);
    UserResponse addAddress(String email, AddressRequest request);
    UserResponse deleteAddress(String email, String addressId);
    UserResponse setDefaultAddress(String email, String addressId);
    User findByEmail(String email);
    
}

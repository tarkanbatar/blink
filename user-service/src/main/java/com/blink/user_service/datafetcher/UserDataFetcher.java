package com.blink.user_service.datafetcher;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import com.blink.user_service.dto.request.AddressRequest;
import com.blink.user_service.dto.request.LoginRequest;
import com.blink.user_service.dto.request.RegisterRequest;
import com.blink.user_service.dto.request.UpdateProfileRequest;
import com.blink.user_service.dto.response.AuthResponse;
import com.blink.user_service.dto.response.UserResponse;
import com.blink.user_service.service.UserService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @DgsComponent: DGS is a GraphQL framework built by Netflix. It connects Queries and Mutations defined in schema.graphqls file to the methods in this class.
 * It acts as a bridge between GraphQL requests and the service layer.
 */

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class UserDataFetcher {

    private final UserService userService;

    @DgsQuery
    @PreAuthorize("isAuthenticated()")
    public UserResponse me(){
        String email = getCurrentUserEmail();
        log.info("Fetching profile for user: {}", email);
        return userService.getCurrentUser(email);
    }

    @DgsQuery
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> users(){
        log.info("Getting all users (admin request)");
        return userService.getAllUsers();
    }

    @DgsMutation
    public AuthResponse register(@InputArgument("input") RegisterRequest input){
        log.info("GraphQL: Register request for: {}", input.getEmail());
        return userService.register(input);
    }
    
    @DgsMutation
    public AuthResponse login(@InputArgument("input") LoginRequest input) {
        log.info("GraphQL: Login request for: {}", input. getEmail());
        return userService.login(input);
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public UserResponse updateProfile(@InputArgument("input") UpdateProfileRequest input) {
        String email = getCurrentUserEmail();
        log. info("GraphQL: Update profile for: {}", email);
        return userService.updateProfile(email, input);
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public UserResponse addAddress(@InputArgument("input") AddressRequest input) {
        String email = getCurrentUserEmail();
        log. info("GraphQL: Add address for: {}", email);
        return userService.addAddress(email, input);
    }

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    public UserResponse deleteAddress(@InputArgument String addressId) {
        String email = getCurrentUserEmail();
        log.info("GraphQL: Delete address {} for: {}", addressId, email);
        return userService.deleteAddress(email, addressId);
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder. getContext().getAuthentication(). getName();
    }
}

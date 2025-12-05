package com.blink.user_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blink.user_service.dto.request.AddressRequest;
import com.blink.user_service.dto.request.UpdateProfileRequest;
import com.blink.user_service.dto.response.UserResponse;
import com.blink.user_service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * @AuthenticationPrincipal: It gets the authenticated user from
     *                           SecurityContext.
     *                           It injects the User object set in
     *                           JwtAuthenticationFilter after successful JWT
     *                           validation.
     */

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching profile for user: {}", userDetails.getUsername());
        UserResponse response = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("Fetching user by id: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Getting all users (admin request)");
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me") // Patch is used here to partially update user profile
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", userDetails.getUsername());
        UserResponse response = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/addresses")
    public ResponseEntity<UserResponse> addAddress(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody AddressRequest request) {
        log.info("Adding address for: {}", userDetails.getUsername());
        UserResponse response = userService.addAddress(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me/addresses/{addressId}")
    public ResponseEntity<UserResponse> deleteAddress(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String addressId) {
        log. info("Deleting address {} for: {}", addressId, userDetails. getUsername());
        UserResponse response = userService.deleteAddress(userDetails.getUsername(), addressId);
        return ResponseEntity. ok(response);
    }

    @PatchMapping("/me/addresses/{addressId}/default")
    public ResponseEntity<UserResponse> setDefaultAddress(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String addressId) {
        log.info("Setting default address {} for: {}", addressId, userDetails.getUsername());
        UserResponse response = userService.setDefaultAddress(userDetails.getUsername(), addressId);
        return ResponseEntity. ok(response);
    }
}

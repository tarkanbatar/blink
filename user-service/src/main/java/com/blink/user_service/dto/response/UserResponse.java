package com.blink.user_service.dto.response;

import java.time.LocalDate;
import java.util.List;
import com.blink.user_service.model.Address;
import com.blink.user_service.model.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private List<Address> addresses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Static factory method to create UserResponse from User entity
    public static UserResponse fromUser(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .firstName(user.getName())
            .lastName(user.getSurname())
            .email(user.getEmail())
            .phone(user.getPhone())
            .addresses(user.getAddresses())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}

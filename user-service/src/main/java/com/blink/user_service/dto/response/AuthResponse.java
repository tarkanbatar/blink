package com.blink.user_service.dto.response;

import lombok.Builder;
import lombok.Data;

/*
    It returns after login/register with JWT token + user details
*/

@Data
@Builder
public class AuthResponse {
    
    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private long expiresIn;
    private UserResponse user;

}

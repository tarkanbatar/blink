package com.blink.user_service.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Lastname must be between 2 and 50 characters")
    private String lastName;

    @Pattern(
        regexp = "^\\\\+?[0-9]{10,14}$",
        message = "Phone number should be valid"
    )   
    private String phone;
}

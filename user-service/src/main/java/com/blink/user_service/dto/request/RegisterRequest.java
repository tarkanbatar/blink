package com.blink.user_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    // Using validation ananotations to ensure data integrity

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password can not be empty")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password should contains 8 characters, 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special character"
    ) 
    private String password;

    @NotBlank(message = "Name can not be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Lastname can not be empty")
    @Size(min = 2, max = 50, message = "Lastname must be between 2 and 50 characters")
    private String lastName;

    @Pattern(
        regexp = "^\\\\+?[0-9]{10,14}$",
        message = "Phone number should be valid"
    )
    private String phone;
}

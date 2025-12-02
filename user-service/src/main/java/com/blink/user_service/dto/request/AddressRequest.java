package com.blink.user_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequest {
    
    @NotBlank(message = "Adress title can not be empty")
    @Size(max = 50, message = "Address title must be less than 50 characters")
    private String title;

    @NotBlank(message = "Address line 1 can not be empty")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City can not be empty")
    private String city;

    @NotBlank(message = "State can not be empty")
    private String state;

    @NotBlank(message = "Zip code can not be empty")
    private String zipCode;

    @NotBlank(message = "Country can not be empty")
    private String country;

    private boolean isDefault = false;
}

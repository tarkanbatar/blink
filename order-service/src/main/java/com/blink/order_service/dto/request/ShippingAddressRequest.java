package com.blink.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
    public class ShippingAddressRequest {
        
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Phone number is required")
        private String phone;

        @NotBlank(message = "Address Line 1 is required")
        private String addressLine1;

        private String addressLine2;

        @NotBlank(message = "City is required")
        private String city;

        private String state;

        @NotBlank(message = "Country is required")
        private String country;

        @NotBlank(message = "Postal code is required")
        private String postalCode;
    }
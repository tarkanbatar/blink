package com.blink.cart_service.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateQuantityRequest {
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}

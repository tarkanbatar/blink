package com.blink.order_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Order status is required")
    private String status;
    
    private String trackingNumber;
    private String cancellationReason;
    private String note;
}

package com.blink.order_service.dto.request;

import com.blink.order_service.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    
    @NotNull(message = "Order status is required")
    private OrderStatus status;
    
    private String trackingNumber;
    private String cancellationReason;
    private String note;
}

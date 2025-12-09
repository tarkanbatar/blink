package com.blink.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOrderRequest {
    //the one which defined in user service
    private String addressId;
    // or manuel address input
    private ShippingAddressRequest  shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String notes;
    private String couponCode;

}

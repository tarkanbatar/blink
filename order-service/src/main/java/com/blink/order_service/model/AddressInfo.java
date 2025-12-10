package com.blink.order_service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressInfo {
    private String id;
    private String title;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}

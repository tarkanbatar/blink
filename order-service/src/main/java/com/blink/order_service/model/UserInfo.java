package com.blink.order_service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfo {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private AddressInfo defaultAddress;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}

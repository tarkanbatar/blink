package com.blink.user_service.model;

import java.util.UUID;
import org.springframework.data.annotation.Id;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String title;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    @Builder.Default
    private boolean isDefault = false;
}

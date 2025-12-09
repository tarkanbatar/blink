package com.blink.cart_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductInfo {
    private String id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Boolean active;

    public boolean isAvailable(int quantity) {
        return active != null && active && stock != null && stock >= quantity;
    }

    public Integer getAvailableQuantity() {
        return stock != null ? stock : 0;
    }
}

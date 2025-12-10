package com.blink.order_service.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemInfo {
    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private int quantity;
}

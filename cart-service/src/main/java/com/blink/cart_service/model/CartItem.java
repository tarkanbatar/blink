package com.blink.cart_service.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

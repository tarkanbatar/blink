package com.blink.order_service.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Single item in an order

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem implements Serializable {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private String productId;
    private String productName;
    private String productImage;
    private String sku;
    private int quantity;
    // price during the order time - it wont change even if product price changes later
    private BigDecimal unitPrice;

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

package com.blink.cart_service.dto.response;

import java.math.BigDecimal;

import com.blink.cart_service.model.CartItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;

    public static CartItemResponse fromEntity(CartItem item) {
        return CartItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
    }
}

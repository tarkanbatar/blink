package com.blink.order_service.dto.response;

import java.math.BigDecimal;

import com.blink.order_service.model.OrderItem;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private String productId;
    private String productName;
    private String productImage;
    private String sku;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal totalPrice;
    
    public static OrderItemResponse fromEntity(OrderItem item) {
        return OrderItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productImage(item.getProductImage())
                .sku(item.getSku())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}

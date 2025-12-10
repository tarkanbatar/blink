package com.blink.order_service.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartInfo {

    private String userId;
    private List<CartItemInfo> items;
    private int totalItems;
    private BigDecimal totalPrice;
    private boolean empty;

    public static CartInfo empty(String userId) {
            return CartInfo.builder()
                    .userId(userId)
                    .items(Collections.emptyList())
                    .totalItems(0)
                    .totalPrice(BigDecimal.ZERO)
                    .empty(true)
                    .build();
        }
}

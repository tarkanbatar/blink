package com.blink.cart_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.blink.cart_service.model.Cart;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {

    private String userId;
    private List<CartItemResponse> items;
    private int totalItems;
    private BigDecimal totalPrice;
    private boolean empty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CartResponse fromEntity(Cart cart){
        return CartResponse.builder()
                .userId(cart.getUserId())
                .items(cart.getItems().stream()
                    .map(CartItemResponse::fromEntity)
                    .collect(Collectors.toList()))
                .totalItems(cart.getTotalItems())
                .totalPrice(cart.getTotalPrice())
                .empty(cart.isEmpty())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    // Empty Cart Response
    public static CartResponse empty(String userId) {
        return CartResponse.builder()
                .userId(userId)
                .items(List.of())
                .totalItems(0)
                .totalPrice(BigDecimal.ZERO)
                .empty(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

package com.blink.order_service.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.blink.order_service.model.CartInfo;
import com.blink.order_service.model.CartItemInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartServiceClient {
    private final WebClient.Builder webClientBuilder;

    /**
     * Get user cart by userId
     */
    @SuppressWarnings("unchecked")
    public CartInfo getCart(String userId) {
        log.debug("Fetching cart for userId: {}", userId);

        try {
            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri("http://cart-service/api/cart")
                    .header("X-User-Id", userId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if(response == null || (Boolean) response.get("empty")) {
                log.debug("Cart is empty for userId: {}", userId);
                return CartInfo.empty(userId);
            }

            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

            return CartInfo.builder()
                    .userId(userId)
                    .items(items.stream().map(this::mapToCartItem).toList())
                    .totalItems((Integer) response.get("totalItems"))
                    .totalPrice(response.get("totalPrice") != null ? new java.math.BigDecimal(response.get("totalPrice").toString()) : null)
                    .empty((Boolean) response.get("empty"))
                    .build();
        } catch (WebClientResponseException.NotFound e){
            log.debug("Cart not found for userId: {}", userId);
            return CartInfo.empty(userId);
        } catch (Exception e) {
            log.error("Error fetching cart for userId: {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to fetch cart data");
        }
    }

    public void clearCart (String userId) {
        log.debug("Cleaning cart for user: {}", userId);

        try {
            webClientBuilder.build()
                .delete()
                .uri("http://cart-service/api/cart")
                .header("X-User-Id", userId)
                .retrieve()
                .bodyToMono(Void.class)
                .block(); // Blocking call to ensure completion
            log.debug("Cart cleared for user: {}", userId);
        } catch (Exception e) {
            log.error("Error clearing cart for userId: {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to clear cart data");
        }
    }

    private CartItemInfo mapToCartItem(Map<String, Object> item) {
        return CartItemInfo.builder()
                        .productId((String) item.get("productId"))
                        .productName((String) item.get("productName"))
                        .productImage((String) item.get("productImage"))
                        .unitPrice(new BigDecimal(item.get("unitPrice").toString()))
                        .quantity((Integer) item.get("quantity"))
                        .build();

    }

}

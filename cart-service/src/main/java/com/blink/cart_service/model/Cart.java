package com.blink.cart_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * USER'S CART MODEL
 * 
 * @RedisHash is going to be added here for caching purposes
 *            Redis key format will be "cart:{userId}"
 */

@RedisHash("cart")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private static final long serialVersionUID = 1L;

    @Id
    private String userId;

    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    // @TimeToLive tells redis to expire this key after certain seconds
    @TimeToLive
    @Builder.Default
    private Long expiration = 7L; // default 7 days

    // ==================== HELPER METHODS ====================

    public int getTotalItems() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // BigDecimal.ZERO is the identity element and
                                                           // BigDecimal::add is the accumulator function
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsProduct(String productId) {
        return items.stream()
                .anyMatch(item -> item.getProductId().equals(productId));
    }

    public CartItem getItem(String productId) {
        return items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);
    }

    public void addItem(CartItem newItem) {
        CartItem existingItem = getItem(newItem.getProductId());

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
        } else {
            items.add(newItem);
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void updateItemQuantity(String productId, int quantity) {
        CartItem item = getItem(productId);
        if (item != null) {
            item.setQuantity(quantity);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        this.updatedAt = LocalDateTime.now();
    }

    public void clearCart() {
        items.clear();
        this.updatedAt = LocalDateTime.now();
    }
}

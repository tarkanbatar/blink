package com.blink.cart_service.service;

import com.blink.cart_service.dto.request.AddToCartRequest;
import com.blink.cart_service.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(String userId);
    CartResponse addToCart(String userId, AddToCartRequest request);
    CartResponse updateItemQuantity(String userId, String productId, int quantity);
    CartResponse removeItem(String userId, String productId);
    void clearCart(String userId);
    void deleteCart(String userId);
    boolean cartExists(String userId);
    
}

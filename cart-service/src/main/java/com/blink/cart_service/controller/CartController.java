package com.blink.cart_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blink.cart_service.dto.request.AddToCartRequest;
import com.blink.cart_service.dto.request.UpdateQuantityRequest;
import com.blink.cart_service.dto.response.CartResponse;
import com.blink.cart_service.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 
 * In general, userId will be extracted from JWT token in the request header.
 * But for simplicity, I'll get it from head like X-User-Id in this example.
 *! TO-DO: Convert it to JWT.
 */

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("X-User-Id") String userId){
        log.info("Received request to get cart for user: {}", userId);
        CartResponse response = cartService.getCart(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(@RequestHeader("X-User-Id") String userId, @Valid @RequestBody AddToCartRequest request){
        log.info("Received request to add product {} to cart for user: {}", request.getProductId(), userId);
        CartResponse response = cartService.addToCart(userId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateQuantity( @RequestHeader("X-User-Id") String userId, @PathVariable String productId, @Valid @RequestBody UpdateQuantityRequest request) {
        log. info("PATCH /api/cart/items/{} - User: {}, Quantity: {}", productId, userId, request.getQuantity());
        CartResponse response = cartService.updateItemQuantity(userId, productId, request.getQuantity());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem( @RequestHeader("X-User-Id") String userId, @PathVariable String productId ) {
        log.info("DELETE /api/cart/items/{} - User: {}", productId, userId);
        CartResponse response = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart( @RequestHeader("X-User-Id") String userId ) {
        log.info("DELETE /api/cart - User: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}

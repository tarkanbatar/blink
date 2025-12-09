package com.blink.cart_service.service.impl;

import org.springframework.stereotype.Service;

import com.blink.cart_service.client.ProductServiceClient;
import com.blink.cart_service.dto.ProductInfo;
import com.blink.cart_service.dto.request.AddToCartRequest;
import com.blink.cart_service.dto.response.CartResponse;
import com.blink.cart_service.exception.CartItemNotFoundException;
import com.blink.cart_service.exception.ProductNotAvailableException;
import com.blink.cart_service.model.Cart;
import com.blink.cart_service.model.CartItem;
import com.blink.cart_service.repository.CartRepository;
import com.blink.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductServiceClient productServiceClient;


    @Override
    public CartResponse getCart(String userId){
        log.info("Getting cart for user: {}", userId);

        return cartRepository.findById(userId)
                        .map(CartResponse::fromEntity)
                        .orElse(CartResponse.empty(userId));
    }

    /**
     * Add to Cart flow: Get Product Info from Product Service -> Check Availability -> Get or Create Cart -> Add Item to Cart -> Save Cart to Redis -> Return CartResponse
     */

    @Override
    public CartResponse addToCart(String userId, AddToCartRequest request) {
        log.info("Adding product {} to cart for user {}", request.getProductId(), userId);

        ProductInfo productInfo = productServiceClient.getProduct(request.getProductId());

        if(productInfo == null) {
            log.warn("Product {} not found", request.getProductId());
            throw new ProductNotAvailableException("Product not found");
        }

        if(!productInfo.isAvailable(request.getQuantity())){
            log.warn("Product {} is not available in requested quantity: {}, Current quantity: {}", request.getProductId(), request.getQuantity(), productInfo.getAvailableQuantity());
            throw new ProductNotAvailableException("Product is not available in requested quantity");
        }

        // get or create cart
        Cart cart = getCartOrThrow(userId);

        // add item to cart with CartItem class
        CartItem item = CartItem.builder()
                            .productId(productInfo.getId())
                            .productName(productInfo.getName())
                            .unitPrice(productInfo.getPrice())
                            .quantity(request.getQuantity())
                            .productImage(productServiceClient.getFirstImage(productInfo))
                            .build();
        cart.addItem(item);

        Cart savedCart = cartRepository.save(cart);
        log.info("Product {} added to cart for user {} quantiy of {}", request.getProductId(), userId, request.getQuantity());

        return CartResponse.fromEntity(savedCart);
    }

    @Override
    public CartResponse updateItemQuantity (String userId, String productId, int quantity) {
        log.info("Updating quantity of product {} to {} for user {}", productId, quantity, userId);

        Cart cart = getCartOrThrow(userId);

        if(!cart.containsProduct(productId)){
            throw new CartItemNotFoundException("Product not found in cart, product id:" + productId);
        }

        ProductInfo productInfo = productServiceClient.getProduct(productId);
        if(productInfo == null || !productInfo.isAvailable(quantity)){
            throw new ProductNotAvailableException(productId, "Product is not available in requested quantity");
        }

        cart.updateItemQuantity(productId, quantity);

        Cart savedCart = cartRepository.save(cart);
        log.info("Quantity of product {} updated to {} for user {}", productId, quantity, userId);

        return CartResponse.fromEntity(savedCart);
    }

    
    @Override
    public CartResponse removeItem(String userId, String productId){
        log.info("Removing product {} from cart for user {}", productId, userId);

        Cart cart = getCartOrThrow(userId);

        if(!cart.containsProduct(productId)){
            throw new CartItemNotFoundException("Product not found in cart, product id:" + productId);
        }

        cart.removeItem(productId);

        if(cart.isEmpty()) {
            cartRepository.deleteById(userId);
            log.info("Cart is empty after removing product {}. Cart deleted for user {}", productId, userId);
            return CartResponse.empty(userId);
        }

        Cart savedCart = cartRepository.save(cart);
        log.info("Product {} removed from cart for user {}", productId, userId);

        return CartResponse.fromEntity(savedCart);
    }

    @Override
    public void clearCart(String userId){
        log.info("Clearing cart for user {}", userId);

        cartRepository.findById(userId).ifPresent(cart -> {
            cart.clearCart();
            cartRepository.save(cart);
        });
    }

    @Override
    public void deleteCart(String userId){
        log.info("Deleting cart for user {}", userId);
        cartRepository.deleteById(userId);
    }

    @Override
    public boolean cartExists(String userId){
        return cartRepository.existsById(userId);
    }


/*    HELPERS */


    private Cart getCartOrThrow(String userId) {
        return cartRepository.findById(userId)
                .orElse(Cart.builder(). userId(userId).build());
    }

}

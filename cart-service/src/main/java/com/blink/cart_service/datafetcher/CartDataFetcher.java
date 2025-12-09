package com.blink.cart_service.datafetcher;

import java.util.Map;

import com.blink.cart_service.dto.request.AddToCartRequest;
import com.blink.cart_service.dto.response.CartResponse;
import com.blink.cart_service.service.CartService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DgsComponent
@RequiredArgsConstructor
@Slf4j
public class CartDataFetcher {

    private final CartService cartService;

    // ======= QUERIES =======

    @DgsQuery
    public CartResponse cart(@InputArgument String userId){
        log.info("GraphQL Query: cart - User: {}", userId);
        return cartService.getCart(userId);
    }

    // ======= MUTATIONS =======

    @DgsMutation
    public CartResponse addToCart(@InputArgument String userId, @InputArgument("input") Map<String, Object> input) {
        log.info("GraphQL Mutation: addToCart - User: {}", userId);

        AddToCartRequest request = new AddToCartRequest();
        request.setProductId((String) input.get("productId"));
        request.setQuantity(input.get("quantity") != null ? (Integer) input.get("quantity") : 1);
        return cartService.addToCart(userId, request);
    }

    @DgsMutation
    public CartResponse updateCartItemQuantity( @InputArgument String userId, @InputArgument String productId, @InputArgument Integer quantity ) {
        log.info("GraphQL: Updating cart item quantity.  User: {}, Product: {}, Qty: {}", userId, productId, quantity);
        return cartService.updateItemQuantity(userId, productId, quantity);
    }
    
    @DgsMutation
    public CartResponse removeFromCart( @InputArgument String userId, @InputArgument String productId ) {
        log.info("GraphQL: Removing cart item. User: {}, Product: {}", userId, productId);
        return cartService.removeItem(userId, productId);
    }

    @DgsMutation
    public boolean clearCart( @InputArgument String userId) {
        log.info("GraphQL: Clearing cart for user: {}", userId);
        cartService.clearCart(userId);
        return true;
    }


}

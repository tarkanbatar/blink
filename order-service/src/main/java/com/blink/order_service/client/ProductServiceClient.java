package com.blink.order_service.client;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.blink.order_service.model.ProductInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {
    
    private final WebClient.Builder webClientBuilder;

    /* Get Product Info */
    @SuppressWarnings("unchecked")
    public ProductInfo getProduct(String productId){
        log.debug("Fetching product: {}", productId);

        try{
            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri("http://product-service/api/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                log.error("Product service returned null for productId: {}", productId);
                return null;
            }

            return ProductInfo.builder()
                    .id((String) response.get("id"))
                    .name((String) response.get("name"))
                    .sku((String) response.get("sku"))
                    .price((BigDecimal) response.get("price"))
                    .build();
        } catch (Exception e){
            log.error("Error fetching product {}: {}", productId, e.getMessage());
            return null;
        }        
    }

    /* Decrease Product Stock */
    public void decreaseStock (String productId, Integer quantity){
        log.debug("Decreasing stock for product: {} by {}", productId, quantity);

        try{
            webClientBuilder.build()
                .patch()
                .uri(uriBuilder -> uriBuilder
                    .path("http://product-service/api/products/{id}/stock")
                    .queryParam("quantity", quantity)
                    .build(productId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch (Exception e){
            log.error("Error decreasing stock for product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to decrease stock for product " + productId);
        }        
    }

    public void increaseStock (String productId, int quantity) {
        log.debug("Increasing stock for product: {} by {}", productId, quantity);

        try{
            webClientBuilder.build()
                .patch()
                .uri(uriBuilder -> uriBuilder
                    .path("http://product-service/api/products/{id}/stock")
                    .queryParam("quantity", quantity)
                    .build(productId))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch (Exception e){
            log.error("Error increasing stock for product {}: {}", productId, e.getMessage());
            throw new RuntimeException("Failed to increase stock for product " + productId);
        }
    }


}

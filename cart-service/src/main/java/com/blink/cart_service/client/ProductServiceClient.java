package com.blink.cart_service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.blink.cart_service.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This is the client to communicate with Product Service.
 * 
 * We used WebClient because:
 * - Non-blocking and asynchronous (reactive programming model)
 * - modern and performant
 * - RestTemplate is deprecated in favor of WebClient
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {
    
    private final WebClient.Builder webClientBuilder;

    /**
     * Return product data
     * @return in the Map: id, name, price, stock, imageUrl, active...
     */

    public ProductInfo getProduct(String productId) {
        log.debug("Fetching product data for id: {}", productId);

        try{
            return (ProductInfo) webClientBuilder.build()
                .get()
                .uri("http://product-service/api/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductInfo.class)
                .block();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Product not found: {}", productId);
            return null;
        } catch (Exception e) {
            log.error("Error fetching product data for id: {}", productId, e);
            throw new RuntimeException("Error fetching product data", e);
        }
    }


    public boolean isProductAvailable(String productId, int quantity) {
        ProductInfo product = getProduct(productId);

        if (product == null || !product.getActive()) 
            return false;
        
        return product.isAvailable(quantity);
    }


    public String getFirstImage(ProductInfo product) {
        return product.getImageUrl();
    }
}

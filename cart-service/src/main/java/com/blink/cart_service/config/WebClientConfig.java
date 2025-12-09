package com.blink.cart_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration class.
 * 
 * @LoadBalanced: Integration with eureka service discovery for load balancing
 * we can send request over an URL like "http://product-service" with service name
 * Load balancer automatically  
 */

@Configuration
public class WebClientConfig {
    
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

}

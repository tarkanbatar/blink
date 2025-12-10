package com.blink.order_service.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.blink.order_service.model.AddressInfo;
import com.blink.order_service.model.UserInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    /** Get User Info */
    @SuppressWarnings("unchecked")
    public UserInfo getUser(String userId) {
        log.debug("Fetching user: {}", userId);

        try {
            Map<String, Object> response = webClientBuilder.build()
                    .get()
                    .uri("http://user-service/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                log.error("User service returned null for userId: {}", userId);
                return null;
            }

            AddressInfo defaultAddress = null;
            
            List<Map<String, Object>> addresses = (List<Map<String, Object>>) response.get("addresses");
            
            if (addresses != null && !addresses.isEmpty()) {
                for(Map<String, Object> addr : addresses) {
                    if(Boolean.TRUE.equals(addr.get("isDefault"))) {
                        defaultAddress = mapToAddress(addr);
                        break;
                    }
                }

                if(defaultAddress == null) {
                    defaultAddress = mapToAddress(addresses.get(0));
                }
            }

            return UserInfo.builder()
                    .id((String) response.get("id"))
                    .email((String) response.get("email"))
                    .firstName((String) response.get("firstName"))
                    .lastName((String) response.get("lastName"))
                    .phone((String) response.get("phone"))
                    .defaultAddress(defaultAddress)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    private AddressInfo mapToAddress(Map<String, Object> addr) {
        return AddressInfo.builder()
                .id((String) addr.get("id"))
                .title((String) addr.get("title"))
                .addressLine1((String) addr.get("addressLine1"))
                .addressLine2((String) addr.get("addressLine2"))
                .city((String) addr.get("city"))
                .state((String) addr.get("state"))
                .country((String) addr.get("country"))
                .zipCode((String) addr.get("zipCode"))
                .build();
    }
}

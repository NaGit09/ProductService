package com.furniro.ProductService.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.order-service.url:http://localhost:8082}")
    private String orderServiceUrl;

    public boolean hasUserPurchasedVariants(Integer userID, List<Integer> variantIDs) {
        if (userID == null || variantIDs == null || variantIDs.isEmpty()) {
            return false;
        }

        try {
            String variantIdsStr = variantIDs.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));

            String url = orderServiceUrl + "/orders/check-purchase?userID=" + userID + "&variantIDs=" + variantIdsStr;

            log.info("Checking purchase status via url: {}", url);
            Boolean response = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(response);
        } catch (Exception e) {
            log.error("Failed to call OrderService check-purchase: {}", e.getMessage());
            return false;
        }
    }
}

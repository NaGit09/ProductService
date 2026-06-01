package com.furniro.ProductService.service.client;

import com.furniro.ProductService.dto.res.RecomProductRes;
import com.furniro.ProductService.dto.res.RecomServiceRes;
import com.furniro.ProductService.utils.RecomReason;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.recommend-service.url}")
    private String recommendServiceUrl;

    public List<RecomProductRes> getRecommendProducts(Integer productID,RecomReason reason) {
        try {
            RestClient restClient = restClientBuilder
                    .baseUrl(recommendServiceUrl)
                    .build();

            RecomServiceRes<List<RecomProductRes>> response =
                    restClient.get()
                            .uri(uriBuilder -> {
                                var builder = uriBuilder.path("/recommend-products/{productID}");

                                if (reason != null) {
                                    builder.queryParam("reason", reason.name());
                                }

                                return builder.build(productID);
                            })
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {});

            if (response == null || response.getData() == null) {
                return Collections.emptyList();
            }

            return response.getData();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
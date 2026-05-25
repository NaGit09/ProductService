package com.furniro.ProductService.service.kafka;

import com.furniro.ProductService.service.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductViewedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.product-viewed}")
    private String productViewedTopic;

    public void send(ProductViewedEvent event) {
        String key = String.valueOf(event.getProductID());

        kafkaTemplate.send(productViewedTopic, key, event);

        log.info("Sent product viewed event. topic={}, productID={}, userID={}, sessionID={}",
                productViewedTopic,
                event.getProductID());
    }
}
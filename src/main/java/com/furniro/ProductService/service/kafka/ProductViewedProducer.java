package com.furniro.ProductService.service.kafka;

import com.furniro.ProductService.service.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductViewedProducer {

    private static final String PRODUCT_VIEWED_TOPIC = "product.viewed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(ProductViewedEvent event) {
        String key = String.valueOf(event.getProductID());

        kafkaTemplate.send(PRODUCT_VIEWED_TOPIC, key, event);

        log.info(
                "Sent product viewed event. topic={}, productID={}",
                PRODUCT_VIEWED_TOPIC,
                event.getProductID()
        );
    }
}
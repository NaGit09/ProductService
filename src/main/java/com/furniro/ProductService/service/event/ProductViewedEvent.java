package com.furniro.ProductService.service.event;

import lombok.*;

import java.time.LocalDateTime;

import com.furniro.ProductService.utils.RecomReason;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductViewedEvent {

    private Integer productID;
    private RecomReason reason;

    private LocalDateTime viewedAt;
}
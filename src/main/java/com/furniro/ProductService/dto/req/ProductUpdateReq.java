package com.furniro.ProductService.dto.req;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import java.math.BigDecimal;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateReq {
    String name;
    String description;
    BigDecimal basePrice;
    String brand;
    String status;

    // Dimensions
    Integer width;
    Integer height;
    Integer depth;
    Integer weight;

    // Materials / configs
    String material;
    String configuration;

    // Warranty
    String warrantyType;
    String warrantyDuration;
    String warrantySummary;
}

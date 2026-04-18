package com.furniro.ProductService.dto.res;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ProductCompareRes {

    private Integer productID;

    private String name;

    private BigDecimal basePrice;

    private String image;

    private Integer width;

    private Integer height;

    private Integer depth;

    private Integer weight;

    private String material;
    
    private String warranty;

}
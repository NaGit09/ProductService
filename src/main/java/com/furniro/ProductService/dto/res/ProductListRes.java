package com.furniro.ProductService.dto.res;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import com.furniro.ProductService.utils.ProductStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductListRes {

    private Integer productID;

    private String name;

    private ProductStatus status;
    
    private String brand;

    private String description;

    private BigDecimal basePrice;

    private String url;

    private Double averageRating;

    private Integer reviewCount;
}
package com.furniro.ProductService.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariantRes {
    private Integer variantID;
    private Integer price;
    private String sku;
    private String color;
    private String size;
}

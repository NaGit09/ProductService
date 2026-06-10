package com.furniro.ProductService.dto.res;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder

public class ProductDetailRecomRes {
     private ProductDetailRes product;
    private List<ProductListRes> recommendProducts;
}

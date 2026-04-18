package com.furniro.ProductService.dto.res;

import java.util.List;

import lombok.Data;

@Data
public class CategoryResponse {
    private Integer categoryID;
    private String categoryName;
    private List<CategoryResponse> subCategories;
}

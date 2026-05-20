package com.furniro.ProductService.dto.mapper;

import org.mapstruct.Mapper;
import com.furniro.ProductService.database.entity.Category;
import com.furniro.ProductService.dto.res.CategoryResponse;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toResponse(Category category);
    List<CategoryResponse> toResponseList(List<Category> categories);
}

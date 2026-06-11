package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Category;
import com.furniro.ProductService.database.repository.CategoryRepository;
import com.furniro.ProductService.dto.res.CategoryResponse;
import com.furniro.ProductService.dto.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryCacheService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Cacheable(value = "category:list")
    public List<CategoryResponse> getRootCategories() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return categoryMapper.toResponseList(rootCategories);
    }
}

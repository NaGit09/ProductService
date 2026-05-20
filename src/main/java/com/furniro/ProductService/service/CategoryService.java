package com.furniro.ProductService.service;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.furniro.ProductService.database.entity.Category;
import com.furniro.ProductService.database.repository.CategoryRepository;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.req.CategoryReq;
import com.furniro.ProductService.dto.mapper.CategoryMapper;

import jakarta.transaction.Transactional;

import com.furniro.ProductService.dto.API.AType;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    
    public ResponseEntity<AType> getAllCategory() {
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return ResponseEntity.ok(ApiType.success(categoryMapper.toResponseList(rootCategories)));
    }

    public ResponseEntity<AType> createCategory
        (CategoryReq categoryDto) {

        if (categoryRepository.existsByCategoryName(categoryDto.getCategoryName())) {
            throw new RuntimeException("Category name already exists");
        }

        Category category = new Category();
        category.setCategoryName(categoryDto.getCategoryName());

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        }

        categoryRepository.save(category);

        return ResponseEntity.ok(ApiType.success(categoryMapper.toResponse(category)));
    }

    public ResponseEntity<AType> updateCategory
        (Integer id, CategoryReq categoryDto) {
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (categoryDto.getCategoryName() != null) {
            category.setCategoryName(categoryDto.getCategoryName());
        }

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        }

        categoryRepository.save(category);

        return ResponseEntity.ok(ApiType.success(categoryMapper.toResponse(category)));
    }
    
    @Transactional
    public void deleteCategory(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getSubCategories().isEmpty()) {
            throw new RuntimeException("Cannot delete category that has sub-categories");
        }

        categoryRepository.delete(category);
    }


}

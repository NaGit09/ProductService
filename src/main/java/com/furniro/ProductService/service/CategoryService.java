package com.furniro.ProductService.service;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.furniro.ProductService.database.entity.Category;
import com.furniro.ProductService.database.repository.CategoryRepository;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.req.CategoryReq;

import jakarta.transaction.Transactional;

import com.furniro.ProductService.dto.API.AType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    
    public ResponseEntity<AType> getAllCategory() {
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Get all category successfully")
                .data(categoryRepository.findAll())
                .build());
    }

    public ResponseEntity<AType> createCategory
        (CategoryReq categoryDto) {

        if (categoryRepository.existsByCategoryName(categoryDto.getName())) {
            throw new RuntimeException("Category name already exists");
        }

        Category category = new Category();
        category.setCategoryName(categoryDto.getName());

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        }

        categoryRepository.save(category);

        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Category created successfully")
                .data(category)
                .build());
    }

    public ResponseEntity<AType> updateCategory
        (Integer id, CategoryReq categoryDto) {
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (categoryDto.getName() != null) {
            category.setCategoryName(categoryDto.getName());
        }

        if (categoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            category.setParentCategory(parent);
        }

        categoryRepository.save(category);

        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Category updated successfully")
                .data(category)
                .build());
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

package com.furniro.ProductService.controller;

import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.req.CategoryReq;
import com.furniro.ProductService.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<AType> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @PostMapping
    public ResponseEntity<AType> createCategory(@RequestBody CategoryReq categoryReq) {
        return categoryService.createCategory(categoryReq);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AType> updateCategory(@PathVariable Integer id, @RequestBody CategoryReq categoryReq) {
        return categoryService.updateCategory(id, categoryReq);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

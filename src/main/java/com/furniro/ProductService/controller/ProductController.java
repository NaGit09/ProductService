package com.furniro.ProductService.controller;

import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<AType> getProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return productService.getProducts(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AType> getProductDetail(@PathVariable Integer id) {
        return productService.getProductDetail(id);
    }

    @PostMapping("/compare")
    public ResponseEntity<AType> compareProducts(@RequestBody List<Integer> ids) {
        return productService.compareProducts(ids);
    }

    @GetMapping("/category/{categoryID}")
    public ResponseEntity<AType> getProductsByCategory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @PathVariable Integer categoryID) {
        return productService.getProductsByCategory(page, size, categoryID);
    }
}

package com.furniro.ProductService.controller;

import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.req.ProductImageReq;
import com.furniro.ProductService.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping("/{id}")
    public ResponseEntity<AType> getProductImage(@PathVariable Integer id) {
        return productImageService.getProductImage(id);
    }

    @GetMapping("/product/{productID}")
    public ResponseEntity<AType> getProductImageByProductID(@PathVariable Integer productID) {
        return productImageService.getProductImageByProductID(productID);
    }

    @PostMapping
    public ResponseEntity<AType> addProductImage(@RequestBody ProductImageReq productImageReq) {
        return productImageService.addProductImage(productImageReq);
    }

    @PutMapping
    public ResponseEntity<AType> updateProductImage(@RequestBody ProductImageReq productImageReq) {
        return productImageService.updateProductImage(productImageReq);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AType> deleteProductImage(@PathVariable Integer id) {
        return productImageService.deleteProductImage(id);
    }
}

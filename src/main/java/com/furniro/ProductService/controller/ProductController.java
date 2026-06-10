package com.furniro.ProductService.controller;

import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/total")
    public ResponseEntity<AType> getTotalProduct() {
        return productService.getTotalProduct();
    }
    
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

    // Wishlist
    @GetMapping("/wishlist-products")
    public ResponseEntity<AType> getWishlistProducts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Integer userId = getUserIdFromJwt(jwt);
        return productService.getWishlistProducts(userId, page, size);
    }

    private Integer getUserIdFromJwt(Jwt jwt) {
        Object userId = jwt.getClaim("userID");

        if (userId == null) {
            userId = jwt.getClaim("accountID");
        }

        if (userId == null) {
            throw new IllegalArgumentException("User ID not found in token");
        }

        return Integer.valueOf(userId.toString());
    }

    @PostMapping("/wishlist-products/{productId}")
    public ResponseEntity<AType> addToWishlist(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer productId) {
        Integer userId = getUserIdFromJwt(jwt);
        return productService.addToWishlist(userId, productId);
    }

    @DeleteMapping("/wishlist-products/{productId}")
    public ResponseEntity<AType> removeFromWishlist(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer productId) {
        Integer userId = getUserIdFromJwt(jwt);
        return productService.removeFromWishlist(userId, productId);
    }


    // Search product
    @GetMapping("/search")
    public ResponseEntity<AType> searchProducts(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam String query) {
        return productService.searchProducts(page, size, query);
    }
    @GetMapping("/category/{categoryID}")
    public ResponseEntity<AType> getProductsByCategory(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @PathVariable Integer categoryID) {
        return productService.getProductsByCategory(page, size, categoryID);
    }
}

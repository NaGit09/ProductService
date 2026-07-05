package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.Wishlist;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.database.repository.WishlistRepository;
import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.mapper.ProductMapper;
import com.furniro.ProductService.dto.res.ProductCompareRes;
import com.furniro.ProductService.dto.res.ProductDetailRes;
import com.furniro.ProductService.dto.res.ProductListRes;
import com.furniro.ProductService.dto.API.ErrorType;
import com.furniro.ProductService.exception.CustomException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final WishlistRepository wishlistRepository;
    private final ProductCacheService productCacheService;

    // Product Management
    public ResponseEntity<AType> getTotalProduct() {
        Long total = productRepository.count();
        return ResponseEntity.ok(ApiType.success(total));
    }

    public ResponseEntity<AType> getProducts(Integer page, Integer size) {
        // 1. validate page and size
        if (page == null || size == null) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }

        // 2. create pageable
        Pageable pageable = PageRequest.of(page, size);

        // 3. find products
        Page<ProductListRes> products = productRepository.getProductList(pageable);

        // 4. response
        return ResponseEntity.ok(ApiType.success(products));
    }

    public ResponseEntity<AType> getProductDetail(Integer id) {
        // 1. validate id
        if (id == null) {
            throw new CustomException(ErrorType.notFound("Product not found"));
        }

        // 2. retrieve cached detail
        ProductDetailRes productDetailRes = productCacheService.getProductDetail(id);

        // 3. response
        return ResponseEntity.ok(ApiType.success(productDetailRes));
    }

    public ResponseEntity<AType> getProductsByCategory(
            Integer page,
            Integer size,
            Integer categoryID) {

        // 1. validate page and size
        if (page == null || size == null) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }

        // 2. validate categoryID
        if (categoryID == null) {
            throw new CustomException(ErrorType.notFound("Category not found"));
        }

        // 3. create pageable
        Pageable pageable = PageRequest.of(page, size);

        // 4. find products
        Page<ProductListRes> products = productRepository.getProductListByCategoryID(pageable, categoryID);

        // 5. response
        return ResponseEntity.ok(ApiType.success(products));
    }

    public ResponseEntity<AType> compareProducts(List<Integer> ids) {
        // 1. validate ids
        if (ids == null || ids.isEmpty()) {
            throw new CustomException(ErrorType.badRequest("Product ids cannot be empty"));
        }

        // 2. check duplicate
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new CustomException(ErrorType.badRequest("Duplicate products detected"));
        }

        // 3. check maximum comparison
        if (ids.size() > 3) {
            throw new CustomException(ErrorType.badRequest("Maximum 3 products can be compared at once"));
        }

        // 4. find products
        List<ProductCompareRes> result = productRepository.compareProducts(ids);

        // 5. response
        return ResponseEntity.ok(ApiType.success(result));
    }

    // Wishlist Management
    public ResponseEntity<AType> getWishlistProducts(Integer userId, Integer page, Integer size) {
        if (userId == null) {
            throw new CustomException(ErrorType.notFound("User not found"));
        }

        if (page == null || size == null || page < 0 || size <= 0) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ProductListRes> products = wishlistRepository.findWishlistProductsByUserId(userId, pageable)
                .map(productMapper::toListRes);

        return ResponseEntity.ok(ApiType.success(products));

    }

    public ResponseEntity<AType> addToWishlist(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            throw new CustomException(ErrorType.notFound("Product not found"));
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Product not found")));

        boolean existed = wishlistRepository.existsByUserIdAndProduct_ProductID(userId, productId);

        if (existed) {
            throw new CustomException(ErrorType.conflict("Product already in wishlist"));
        }

        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .product(product)
                .build();

        wishlistRepository.save(wishlist);

        return ResponseEntity.ok(ApiType.success("Added product to wishlist successfully"));
    }

    public ResponseEntity<AType> removeFromWishlist(Integer userId, Integer productId) {
        if (userId == null) {
            throw new CustomException(ErrorType.notFound("User not found"));
        }

        if (productId == null) {
            throw new CustomException(ErrorType.notFound("Product not found"));
        }

        Wishlist wishlist = wishlistRepository
                .findByUserIdAndProduct_ProductID(userId, productId)
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Product not found in wishlist")));

        wishlistRepository.delete(wishlist);

        return ResponseEntity.ok(ApiType.success("Removed product from wishlist successfully"));
    }

    // Search Management
    public ResponseEntity<AType> searchProducts(Integer page, Integer size, String query) {
        if (page == null || size == null) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }
        if (query == null || query.isEmpty()) {
            throw new CustomException(ErrorType.badRequest("Invalid search query"));
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductListRes> products = productRepository.searchProducts(pageable, query.trim());
        return ResponseEntity.ok(ApiType.success(products));
    }


}
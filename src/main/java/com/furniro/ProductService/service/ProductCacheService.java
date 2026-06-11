package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.dto.res.ProductDetailRes;
import com.furniro.ProductService.dto.mapper.ProductMapper;
import com.furniro.ProductService.exception.ProductException;
import com.furniro.ProductService.utils.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCacheService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = "product:detail", key = "#id")
    public ProductDetailRes getProductDetail(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toDetailRes(product);
    }
}

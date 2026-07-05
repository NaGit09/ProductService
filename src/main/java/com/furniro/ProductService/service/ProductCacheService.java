package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.dto.res.ProductDetailRes;
import com.furniro.ProductService.dto.mapper.ProductMapper;
import com.furniro.ProductService.exception.CustomException;
import com.furniro.ProductService.dto.API.ErrorType;
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
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Product not found")));
        return productMapper.toDetailRes(product);
    }
}

package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.mapper.ProductMapper;
import com.furniro.ProductService.dto.res.ProductCompareRes;
import com.furniro.ProductService.dto.res.ProductDetailRes;
import com.furniro.ProductService.dto.res.ProductListRes;
import com.furniro.ProductService.exception.ProductException;
import com.furniro.ProductService.utils.ProductErrorCode;

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

    public ResponseEntity<AType> getProducts(Integer page, Integer size) {
        // 1. validate page and size
        if (page == null || size == null) {
            throw new ProductException(ProductErrorCode.INVALID_PAGE_SIZE);
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
            throw new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        // 2. find product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 3. map to response
        ProductDetailRes productDetailRes = productMapper.toDetailRes(product);

        // 4. response
        return ResponseEntity.ok(ApiType.success(productDetailRes));
    }

    public ResponseEntity<AType> compareProducts(List<Integer> ids) {
        // 1. validate ids
        if (ids == null || ids.isEmpty()) {
            throw new ProductException(ProductErrorCode.PRODUCT_EMPTY);
        }

        // 2. check duplicate
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new ProductException(ProductErrorCode.DUPLICATE_PRODUCT);
        }

        // 3. check maximum comparison
        if (ids.size() > 3) {
            throw new ProductException(ProductErrorCode.MAXIMUM_COMPARISON);
        }

        // 4. find products
        List<ProductCompareRes> result = productRepository.compareProducts(ids);

        // 5. response
        return ResponseEntity.ok(ApiType.success(result));
    }

    public ResponseEntity<AType> getProductsByCategory(
        Integer page,
        Integer size,
        Integer categoryID) {
        
        //1. validate page and size
        if (page == null || size == null) {
            throw new ProductException(ProductErrorCode.INVALID_PAGE_SIZE);
        }

        // 2. validate categoryID
        if (categoryID == null) {
            throw new ProductException(ProductErrorCode.CATEGORY_NOT_FOUND);
        }

        //3. create pageable
        Pageable pageable = PageRequest.of(page, size);

        // 4. find products
        Page<ProductListRes> products = productRepository.getProductListByCategoryID(pageable, categoryID);

        // 5. response
        return ResponseEntity.ok(ApiType.success(products));
    }

    
}

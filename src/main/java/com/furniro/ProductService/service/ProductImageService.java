package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.ProductImage;
import com.furniro.ProductService.database.repository.ProductImageRepository;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.req.ProductImageReq;
import com.furniro.ProductService.exception.ProductException;
import com.furniro.ProductService.utils.ProductErrorCode;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;

    public ResponseEntity<AType> getProductImage(Integer id) {
        //1. validate id
        if(id == null){
            throw new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }
        
        //2. get product image by id
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND));
        
        //3. response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Get product image successfully")
                .data(productImage)
                .build());
    }

    public ResponseEntity<AType> getProductImageByProductID
        (Integer productID) {
        //1. check product is existed
        productRepository.findById(productID)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        //2. get product image by product ID
        List<ProductImage> productImage = productImageRepository
                .findByProduct_ProductID(productID);

        //3. check product image is existed
        if(productImage.isEmpty()){
            throw new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }
        
        //4. response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Get product image by product ID successfully")
                .data(productImage)
                .build());
    }

    public ResponseEntity<AType> addProductImage
        (ProductImageReq productImageReq) {

        // 1. check product is existed
        Product product = productRepository.findById(productImageReq.getProductID())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));


        // 2. create product image 
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setUrl(productImageReq.getUrl());
        productImage.setSortOrder(productImageReq.getSortOrder());
        
        productImageRepository.save(productImage);

        // 3. emit kafka event : product.image.active
        // kafkaTemplate.send("product.image.active", productImage);

        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Add product image successfully")
                .data(productImage)
                .build());
    }

    public ResponseEntity<AType> updateProductImage
        (ProductImageReq productImageReq) {
        //1. validate id
        if (productImageReq.getId() == null) {
            throw new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }

        //2. get product image by id
        ProductImage productImage = productImageRepository.findById(productImageReq.getId())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND));

        //3. check product is existed
        Product product = productRepository.findById(productImageReq.getProductID())
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        //4. update product image
        productImage.setProduct(product);
        productImage.setUrl(productImageReq.getUrl());
        productImage.setSortOrder(productImageReq.getSortOrder());

        productImageRepository.save(productImage);

        // 5. emit kafka event : product.image.update

        //6. response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Update product image successfully")
                .data(productImage)
                .build());
    }

    public ResponseEntity<AType> deleteProductImage(Integer id) {
        //1. validate id
        if (id == null) {
            throw new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }

        //2. get product image by id
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND));

        //3. delete product image
        productImageRepository.delete(productImage);

        // 4. emit kafka event : product.image.delete

        //5. response
        return ResponseEntity.ok(ApiType.builder()
                .code(200)
                .message("Delete product image successfully")
                .data(productImage)
                .build());
    }

}

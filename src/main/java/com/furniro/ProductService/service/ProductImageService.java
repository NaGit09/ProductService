package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.ProductImage;
import com.furniro.ProductService.database.repository.ProductImageRepository;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.req.ProductImageReq;
import com.furniro.ProductService.exception.ProductException;
import com.furniro.ProductService.service.kafka.KafkaProducer;
import com.furniro.ProductService.utils.ProductErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final KafkaProducer producer;

    public ResponseEntity<AType> getProductImage(Integer id) {
        //1. validate id
        if(id == null){
            throw new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }
        
        //2. get product image by id
        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_IMAGE_NOT_FOUND));
        
        //3. response
        return ResponseEntity.ok(ApiType.success(productImage));
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
        return ResponseEntity.ok(ApiType.success(productImage));
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

        // 3. emit kafka event : upload.active
        Map<String, Object> message = new HashMap<>();
        message.put("fileID", productImage.getImageID());
        producer.send("upload.active", message);

        // 4. response
        return ResponseEntity.ok(ApiType.success(productImage));
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

        // 5. emit kafka event : upload.active
        Map<String, Object> message = new HashMap<>();
        message.put("fileID", productImage.getImageID());
        producer.send("upload.active", message);

        //6. response
        return ResponseEntity.ok(ApiType.success(productImage));
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

        // 4. emit kafka event : upload.delete
        Map<String, Object> message = new HashMap<>();
        message.put("fileID", productImage.getImageID());
        producer.send("upload.delete", message);
        
        //5. response
        return ResponseEntity.ok(ApiType.success(null));
    }

}

package com.furniro.ProductService.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProductErrorCode {

    CATEGORY_NOT_FOUND(404, "Category not found", HttpStatus.NOT_FOUND),

    PRODUCT_NOT_FOUND(404, "Product not found", HttpStatus.NOT_FOUND),

    INVALID_PAGE_SIZE(404, "Invalid page size", HttpStatus.BAD_REQUEST),

    PRODUCT_EMPTY(404, "Product ids cannot be empty", HttpStatus.BAD_REQUEST),

    DUPLICATE_PRODUCT(404, "Duplicate products detected", HttpStatus.BAD_REQUEST),

    MAXIMUM_COMPARISON(404, "Maximum 3 products can be compared at once", HttpStatus.BAD_REQUEST),

    PRODUCT_IMAGE_NOT_FOUND(404, "Product image not found", HttpStatus.NOT_FOUND);

    private final int code; // Business error code (dùng trong response JSON)
    private final String message; // Message trả về client
    private final HttpStatus httpStatus; // HTTP status code thực tế

    ProductErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
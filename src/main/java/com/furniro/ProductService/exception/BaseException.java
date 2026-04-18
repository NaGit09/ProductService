package com.furniro.ProductService.exception;

import com.furniro.ProductService.dto.API.ErrorType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {
    private ErrorType errorType;

    public BaseException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }


    public int getCode() {
        return this.errorType.getCode();
    }
}

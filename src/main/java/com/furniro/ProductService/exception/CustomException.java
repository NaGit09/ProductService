package com.furniro.ProductService.exception;

import com.furniro.ProductService.dto.API.ErrorType;


public class CustomException extends RuntimeException {
    
    private ErrorType errorCode;

    public CustomException(ErrorType errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorType errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorType getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorType errorCode) {
        this.errorCode = errorCode;
    }
}
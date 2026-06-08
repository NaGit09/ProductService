package com.furniro.ProductService.exception;

import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ErrorType;
import com.furniro.ProductService.utils.ProductErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<AType> handleProductException(ProductException ex) {
        ProductErrorCode errorCode = ex.getErrorCode();

        AType error = ErrorType.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(error);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<AType> handleBaseException(BaseException ex) {
        AType error = ErrorType.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.valueOf(ex.getCode()))
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AType> handleUnknownException(Exception ex) {
        log.error("Unhandled exception", ex);

        AType error = ErrorType.builder()
                .code(500)
                .message("Internal server error")
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}
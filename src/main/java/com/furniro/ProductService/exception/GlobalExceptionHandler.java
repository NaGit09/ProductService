package com.furniro.ProductService.exception;

import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<AType> handleCustomException(CustomException ex) {
        ErrorType error = ex.getErrorCode();

        AType responseError = ErrorType.builder()
                .code(error.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.valueOf(error.getCode()))
                .body(responseError);
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
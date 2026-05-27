package com.furniro.ProductService.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecomServiceRes<T> {
    private Integer code;
    private T data;
    private String message;
}
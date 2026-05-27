package com.furniro.ProductService.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecomProductRes {
    private Integer productID;
    private Integer score;
    private String reason;
}
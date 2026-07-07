package com.furniro.ProductService.dto.req;

import lombok.Data;

@Data
public class ReviewReq {
    private Integer rating;
    private String comment;
}

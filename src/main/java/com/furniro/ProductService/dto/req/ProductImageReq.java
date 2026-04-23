package com.furniro.ProductService.dto.req;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageReq {

    private Integer id;

    private Integer productID;

    private String url;

    private Integer sortOrder;

}

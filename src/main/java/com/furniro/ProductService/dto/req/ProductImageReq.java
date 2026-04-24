package com.furniro.ProductService.dto.req;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageReq {

    // can have not id (when create) and have id (when update)
    private Integer id;

    private Integer productID;

    private String url;

    private Integer sortOrder;

}

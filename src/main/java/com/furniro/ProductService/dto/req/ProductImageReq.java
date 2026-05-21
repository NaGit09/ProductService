package com.furniro.ProductService.dto.req;

import lombok.Data;
import lombok.experimental.FieldDefaults;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageReq {

    // can has not id (when create) and has id (when update)
    private Integer id;

    private Integer productID;

    private Integer imageID;
    
    private String url;

    // Allow null (when create) and not null (when update)
    @Nullable()
    private Integer sortOrder;

}

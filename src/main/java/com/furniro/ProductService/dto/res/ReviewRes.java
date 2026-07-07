package com.furniro.ProductService.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRes {
    private Integer reviewID;
    private Integer productID;
    private Integer userID;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

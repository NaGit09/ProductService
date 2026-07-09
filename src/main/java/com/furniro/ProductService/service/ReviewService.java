package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.ProductVariant;
import com.furniro.ProductService.database.entity.Review;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.database.repository.ProductVariantRepository;
import com.furniro.ProductService.database.repository.ReviewRepository;
import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.API.ErrorType;
import com.furniro.ProductService.dto.req.ReviewReq;
import com.furniro.ProductService.dto.res.ReviewRes;
import com.furniro.ProductService.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final OrderServiceClient orderServiceClient;
    private final org.springframework.cache.CacheManager cacheManager;

    public ResponseEntity<AType> getProductReviews(Integer productID) {
        List<Review> reviews = reviewRepository.findByProduct_ProductID(productID);
        List<ReviewRes> response = reviews.stream()
                .map(r -> ReviewRes.builder()
                        .reviewID(r.getReviewID())
                        .productID(r.getProduct().getProductID())
                        .userID(r.getUserID())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiType.success(response));
    }

    @Transactional
    public ResponseEntity<AType> addProductReview(Integer userID, Integer productID, ReviewReq req) {
        // 1. Validate rating
        if (req.getRating() == null || req.getRating() < 1 || req.getRating() > 5) {
            throw new CustomException(ErrorType.badRequest("Rating must be between 1 and 5"));
        }

        // 2. Validate product exists
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Product not found")));

        // 3. Find all variant IDs of this product
        List<ProductVariant> variants = productVariantRepository.findByProduct_ProductID(productID);
        if (variants.isEmpty()) {
            throw new CustomException(ErrorType.badRequest("Product has no variants and cannot be bought or reviewed"));
        }

        List<Integer> variantIDs = variants.stream()
                .map(v -> v.getVariantID())
                .toList();

        // 4. Check if user has purchased at least one of these variants
        boolean hasPurchased = orderServiceClient.hasUserPurchasedVariants(userID, variantIDs);
        if (!hasPurchased) {
            throw new CustomException(ErrorType.badRequest("You must purchase this product before you can leave feedback."));
        }

        // 5. Create and save review
        Review review = new Review();
        review.setProduct(product);
        review.setUserID(userID);
        review.setRating(req.getRating());
        review.setComment(req.getComment());

        reviewRepository.save(review);

        // 6. Recalculate average rating and review count
        List<Review> reviews = reviewRepository.findByProduct_ProductID(productID);
        double totalRating = 0;
        for (Review r : reviews) {
            totalRating += r.getRating();
        }
        double avgRating = reviews.isEmpty() ? 0.0 : totalRating / reviews.size();
        product.setAverageRating(avgRating);
        product.setReviewCount(reviews.size());
        productRepository.save(product);

        // 7. Evict product detail cache
        Cache cache = cacheManager.getCache("product:detail");
        if (cache != null) {
            cache.evict(productID);
        }

        ReviewRes response = ReviewRes.builder()
                .reviewID(review.getReviewID())
                .productID(product.getProductID())
                .userID(userID)
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiType.success(response));
    }
}

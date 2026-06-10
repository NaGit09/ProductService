package com.furniro.ProductService.database.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    @Query("""
                SELECT w.product
                FROM Wishlist w
                WHERE w.userId = :userId
            """)
    Page<Product> findWishlistProductsByUserId(
            @Param("userId") Integer userId,
            Pageable pageable);

    boolean existsByUserIdAndProduct_ProductID(Integer userId, Integer productId);

    Optional<Wishlist> findByUserIdAndProduct_ProductID(Integer userId, Integer productId);
}

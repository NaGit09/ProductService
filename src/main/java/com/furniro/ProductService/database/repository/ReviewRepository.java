package com.furniro.ProductService.database.repository;

import com.furniro.ProductService.database.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProduct_ProductID(Integer productID);
}

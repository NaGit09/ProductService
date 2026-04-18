package com.furniro.ProductService.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.furniro.ProductService.database.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);

    List<Category> findByParentCategory(Category parentCategory);


    // Lấy tất cả danh mục gốc (cha cao nhất) để xây dựng cây
    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL")
    List<Category> findRootCategories();

    // Kiểm tra xem tên danh mục đã tồn tại chưa
    boolean existsByCategoryName(String categoryName);
}

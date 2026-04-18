package com.furniro.ProductService.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.dto.res.ProductCompareRes;
import com.furniro.ProductService.dto.res.ProductListRes;
import com.furniro.ProductService.utils.ProductStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>,
        JpaSpecificationExecutor<Product> {

    Page<Product> findByCategory_CategoryIDAndStatus(
            Integer categoryId, ProductStatus status, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(
            String name, Pageable pageable);

    @Query("""
                SELECT new com.furniro.ProductService.dto.res.ProductListRes(
                    p.productID,
                    p.name,
                    p.status,
                    p.brand,
                    p.description,
                    p.basePrice,
                    pi.url
                )
                FROM Product p
                LEFT JOIN p.images pi ON pi.sortOrder = 0
                WHERE p.status = 'ACTIVE'
            """)
    Page<ProductListRes> getProductList(Pageable pageable);

    @NullMarked
    Optional<Product> findById(Integer id);

    @Query("""
                SELECT new com.furniro.ProductService.dto.res.ProductCompareRes(
                    p.productID,
                    p.name,
                    p.basePrice,
                    pi.url,
                    s.width, s.height, s.depth, s.weight, s.material,
                    w.type
                )
                FROM Product p
                LEFT JOIN p.specification s
                LEFT JOIN p.warranty w
                LEFT JOIN p.images pi ON pi.sortOrder = 0
                WHERE p.productID IN :ids
            """)
    List<ProductCompareRes> compareProducts(List<Integer> ids);

    @Query("""
                SELECT new com.furniro.ProductService.dto.res.ProductListRes(
                    p.productID,
                    p.name,
                    p.status,
                    p.brand,
                    p.description,
                    p.basePrice,
                    pi.url
                )
                FROM Product p
                LEFT JOIN p.images pi ON pi.sortOrder = 0
                WHERE p.status = 'ACTIVE' AND p.category.categoryID = :categoryID
            """)
    Page<ProductListRes> getProductListByCategoryID(Pageable pageable, Integer categoryID);
}
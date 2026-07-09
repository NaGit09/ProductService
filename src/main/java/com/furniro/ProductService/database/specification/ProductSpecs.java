package com.furniro.ProductService.database.specification;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.ProductVariant;
import com.furniro.ProductService.database.entity.ProductSpecification;
import com.furniro.ProductService.database.entity.ColorMaster;
import com.furniro.ProductService.database.entity.SizeMaster;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecs {

    public static Specification<Product> hasKeyword(String query) {
        return (root, criteriaQuery, cb) -> {
            if (query == null || query.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + query.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("brand")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Product> hasCategory(Integer categoryID) {
        return (root, criteriaQuery, cb) -> {
            if (categoryID == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("category").get("categoryID"), categoryID);
        };
    }

    public static Specification<Product> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, criteriaQuery, cb) -> {
            if (minPrice == null && maxPrice == null) {
                return cb.conjunction();
            }
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("basePrice"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice);
            } else {
                return cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice);
            }
        };
    }

    public static Specification<Product> hasColor(Integer colorID) {
        return (root, criteriaQuery, cb) -> {
            if (colorID == null) {
                return cb.conjunction();
            }
            Join<Product, ProductVariant> variantsJoin = root.join("variants", JoinType.LEFT);
            Join<ProductVariant, ColorMaster> colorJoin = variantsJoin.join("color", JoinType.LEFT);
            return cb.equal(colorJoin.get("colorID"), colorID);
        };
    }

    public static Specification<Product> hasSize(Integer sizeID) {
        return (root, criteriaQuery, cb) -> {
            if (sizeID == null) {
                return cb.conjunction();
            }
            Join<Product, ProductVariant> variantsJoin = root.join("variants", JoinType.LEFT);
            Join<ProductVariant, SizeMaster> sizeJoin = variantsJoin.join("size", JoinType.LEFT);
            return cb.equal(sizeJoin.get("sizeID"), sizeID);
        };
    }

    public static Specification<Product> hasMaterial(String material) {
        return (root, criteriaQuery, cb) -> {
            if (material == null || material.trim().isEmpty()) {
                return cb.conjunction();
            }
            Join<Product, ProductSpecification> specJoin = root.join("specification", JoinType.LEFT);
            return cb.like(cb.lower(specJoin.get("material")), "%" + material.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Product> isActive() {
        return (root, criteriaQuery, cb) -> cb.equal(root.get("status"), com.furniro.ProductService.utils.ProductStatus.ACTIVE);
    }
}

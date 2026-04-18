package com.furniro.ProductService.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductVariant")
@Getter
@Setter
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer variantID;

    @Column(unique = true)
    private String sku;

    private Integer price;

    @Column(columnDefinition = "integer default 0")
    private Integer stockQuantity = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "productID", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colorID", nullable = false)
    private ColorMaster color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sizeID", nullable = false)
    private SizeMaster size;
}
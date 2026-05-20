package com.furniro.ProductService.database.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "ProductSpecification")
@Getter
@Setter
public class ProductSpecification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer specID;

    private Integer width,
            height,
            depth,
            weight;

    private String material,
            configuration;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productID", nullable = false)
    @JsonBackReference
    private Product product;
}
package com.furniro.ProductService.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProductImage")
@Getter
@Setter
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageID;

    private String url;

    private Integer sortOrder = 0;

    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;
}
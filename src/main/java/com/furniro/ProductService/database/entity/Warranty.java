package com.furniro.ProductService.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Warranty")
@Getter
@Setter
public class Warranty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer warrantyID;

    private String type,
            duration;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productID", nullable = false)
    private Product product;
}
package com.furniro.ProductService.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.furniro.ProductService.utils.LicenseType;

@Entity
@Table(name = "ProductLicense")
@Getter
@Setter
public class ProductLicense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer licenseID;

    private String licenseName;

    @Enumerated(EnumType.STRING)
    private LicenseType licenseType;

    private String documentUrl;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;
}
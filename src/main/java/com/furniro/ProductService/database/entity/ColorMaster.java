package com.furniro.ProductService.database.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ColorMaster")
@Getter
@Setter
public class ColorMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer colorID;
    private String colorName;
}

package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "product_name", length = 255)
    private String productName;
    
    @Column(name = "product_type", length = 50)
    private String productType; // SAVINGS, TERM_DEPOSIT, SAVINGS_POT, PERSONAL_FINANCING
    
    @Column(name = "product_category", length = 100)
    private String productCategory;
    
    @Column(name = "margin_rate", precision = 5, scale = 2)
    private BigDecimal marginRate;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}


package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "customer_products")
@Data
@IdClass(CustomerProductId.class)
public class CustomerProduct {
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;
    
    @Column(name = "status", length = 50)
    private String status; // ACTIVE, CLOSED
    
    @Column(name = "usage_frequency")
    private Integer usageFrequency;
}


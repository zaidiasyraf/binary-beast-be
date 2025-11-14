package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_relationships")
@Data
public class CustomerRelationship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relationship_id")
    private Long relationshipId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id_1", nullable = false)
    private Customer customer1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id_2", nullable = false)
    private Customer customer2;
    
    @Column(name = "relationship_type", length = 50)
    private String relationshipType; // SUPPLIER, CUSTOMER, PARTNER
    
    @Column(name = "transaction_count")
    private Integer transactionCount;
    
    @Column(name = "total_volume", precision = 15, scale = 2)
    private BigDecimal totalVolume;
    
    @Column(name = "first_transaction_date")
    private LocalDate firstTransactionDate;
    
    @Column(name = "last_transaction_date")
    private LocalDate lastTransactionDate;
    
    @Column(name = "relationship_strength", precision = 5, scale = 2)
    private BigDecimal relationshipStrength;
    
    @Column(name = "embedding", columnDefinition = "vector(1024)")
    private String embedding;
    
    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}


package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;
    
    @Column(name = "customer_type", length = 50)
    private String customerType; // PERSONAL, SME, RETAIL
    
    @Column(name = "customer_segment", length = 50)
    private String customerSegment; // B40, M40, T20
    
    @Column(name = "name", length = 255)
    private String name;
    
    @Column(name = "email", length = 255)
    private String email;
    
    @Column(name = "phone", length = 50)
    private String phone;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "state", length = 100)
    private String state;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(name = "country", length = 100)
    private String country;
    
    @Column(name = "registration_date")
    private LocalDate registrationDate;
    
    @Column(name = "status", length = 50)
    private String status; // ACTIVE, INACTIVE, DISENGAGED
    
    @Column(name = "classification", length = 50)
    private String classification; // BEGINNER, LEADER, HIDDEN_BUSINESS
    
    // Vector embedding stored as PostgreSQL vector type
    // Note: Will need custom type converter for proper handling
    @Column(name = "embedding", columnDefinition = "vector(1024)")
    private String embedding;
    
    // Pre-computed metrics
    @Column(name = "transaction_frequency")
    private Integer transactionFrequency;
    
    @Column(name = "login_frequency")
    private Integer loginFrequency;
    
    @Column(name = "savings_deposit_frequency")
    private Integer savingsDepositFrequency;
    
    @Column(name = "month_over_month_growth", precision = 15, scale = 2)
    private BigDecimal monthOverMonthGrowth;
    
    @Column(name = "profitability_score", precision = 5, scale = 2)
    private BigDecimal profitabilityScore;
    
    @Column(name = "engagement_score", precision = 5, scale = 2)
    private BigDecimal engagementScore;
    
    @Column(name = "digital_feature_score", precision = 5, scale = 2)
    private BigDecimal digitalFeatureScore;
    
    @Column(name = "disengagement_risk_score", precision = 5, scale = 2)
    private BigDecimal disengagementRiskScore;
    
    @Column(name = "graduation_potential_score", precision = 5, scale = 2)
    private BigDecimal graduationPotentialScore;
    
    @Column(name = "content_text", columnDefinition = "TEXT")
    private String contentText;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "digital_feature_usage")
@Data
public class DigitalFeatureUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "feature_name", length = 100)
    private String featureName; // QR_PAY, TRANSFER, BILL_PAYMENT, INVESTMENT
    
    @Column(name = "usage_count")
    private Integer usageCount;
    
    @Column(name = "last_used_date")
    private LocalDate lastUsedDate;
    
    @Column(name = "usage_frequency", length = 50)
    private String usageFrequency; // DAILY, WEEKLY, MONTHLY
    
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;
}


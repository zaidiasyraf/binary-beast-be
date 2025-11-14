package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long accountId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "account_type", length = 50)
    private String accountType; // SAVINGS, TERM_DEPOSIT, SAVINGS_POT, BUSINESS
    
    @Column(name = "account_number", length = 100)
    private String accountNumber;
    
    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;
    
    @Column(name = "opening_date")
    private LocalDate openingDate;
    
    @Column(name = "status", length = 50)
    private String status; // ACTIVE, CLOSED, FROZEN
    
    @Column(name = "embedding", columnDefinition = "vector(1536)")
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


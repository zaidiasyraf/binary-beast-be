package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "counterparty_customer_id")
    private Customer counterpartyCustomer;
    
    @Column(name = "transaction_type", length = 50)
    private String transactionType; // DEPOSIT, WITHDRAWAL, TRANSFER, QR_PAYMENT
    
    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "direction", length = 20)
    private String direction; // INFLOW, OUTFLOW
    
    @Column(name = "transaction_date")
    private LocalDate transactionDate;
    
    @Column(name = "channel", length = 50)
    private String channel; // DIGITAL, BRANCH, ATM, QR
    
    @Column(name = "category", length = 100)
    private String category; // SAVINGS_DEPOSIT, BUSINESS_INCOME, SIDE_INCOME
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "location", columnDefinition = "TEXT")
    private String location;
    
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


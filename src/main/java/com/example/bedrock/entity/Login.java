package com.example.bedrock.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "logins")
@Data
public class Login {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_id")
    private Long loginId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "login_date")
    private LocalDateTime loginDate;
    
    @Column(name = "login_channel", length = 50)
    private String loginChannel; // MOBILE_APP, WEB
    
    @Column(name = "session_duration")
    private Integer sessionDuration; // in seconds
    
    @Column(name = "device_type", length = 50)
    private String deviceType;
}


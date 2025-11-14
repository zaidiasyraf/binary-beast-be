package com.example.bedrock.repository;

import com.example.bedrock.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
    
    List<Login> findByCustomer_CustomerId(Long customerId);
    
    List<Login> findByLoginChannel(String loginChannel);
    
    List<Login> findByLoginDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find recent logins for a customer
    @Query("SELECT l FROM Login l WHERE l.customer.customerId = :customerId ORDER BY l.loginDate DESC")
    List<Login> findRecentLoginsByCustomerId(@Param("customerId") Long customerId);
}


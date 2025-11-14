package com.example.bedrock.repository;

import com.example.bedrock.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    List<Account> findByCustomer_CustomerId(Long customerId);
    
    List<Account> findByAccountType(String accountType);
    
    List<Account> findByStatus(String status);
    
    // Vector similarity search
    @Query(value = "SELECT a.*, 1 - (a.embedding <=> CAST(:embedding AS vector)) AS similarity " +
                   "FROM accounts a " +
                   "WHERE a.embedding IS NOT NULL " +
                   "ORDER BY a.embedding <=> CAST(:embedding AS vector) " +
                   "LIMIT :limit", nativeQuery = true)
    List<Account> findSimilarAccounts(@Param("embedding") String embedding, @Param("limit") int limit);
    
    // Find accounts by customer and type
    @Query("SELECT a FROM Account a WHERE a.customer.customerId = :customerId AND a.accountType = :accountType")
    List<Account> findByCustomerIdAndAccountType(@Param("customerId") Long customerId, 
                                                  @Param("accountType") String accountType);
}


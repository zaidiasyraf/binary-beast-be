package com.example.bedrock.repository;

import com.example.bedrock.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByCustomer_CustomerId(Long customerId);
    
    List<Transaction> findByAccount_AccountId(Long accountId);
    
    List<Transaction> findByTransactionType(String transactionType);
    
    List<Transaction> findByTransactionDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Vector similarity search
    @Query(value = "SELECT t.*, 1 - (t.embedding <=> CAST(:embedding AS vector(1536))) AS similarity " +
                   "FROM transactions t " +
                   "WHERE t.embedding IS NOT NULL " +
                   "ORDER BY t.embedding <=> CAST(:embedding AS vector(1536)) " +
                   "LIMIT :limit", nativeQuery = true)
    List<Transaction> findSimilarTransactions(@Param("embedding") String embedding, @Param("limit") int limit);
    
    // Find transactions by customer and date range
    @Query("SELECT t FROM Transaction t WHERE t.customer.customerId = :customerId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByCustomerIdAndDateRange(@Param("customerId") Long customerId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);
    
    // Find transactions by category
    @Query("SELECT t FROM Transaction t WHERE t.category = :category ORDER BY t.transactionDate DESC")
    List<Transaction> findByCategory(@Param("category") String category);
    
    // Update embedding with proper vector casting
    @Modifying
    @Transactional
    @Query(value = "UPDATE transactions SET embedding = CAST(:embedding AS vector(1536)) WHERE transaction_id = :transactionId", nativeQuery = true)
    void updateTransactionEmbedding(@Param("transactionId") Long transactionId, @Param("embedding") String embedding);
}


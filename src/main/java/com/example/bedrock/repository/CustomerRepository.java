package com.example.bedrock.repository;

import com.example.bedrock.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    List<Customer> findByCustomerType(String customerType);
    
    List<Customer> findByCustomerSegment(String customerSegment);
    
    List<Customer> findByStatus(String status);
    
    List<Customer> findByClassification(String classification);
    
    // Vector similarity search using cosine distance
    @Query(value = "SELECT c.*, 1 - (c.embedding <=> CAST(:embedding AS vector)) AS similarity " +
                   "FROM customers c " +
                   "WHERE c.embedding IS NOT NULL " +
                   "ORDER BY c.embedding <=> CAST(:embedding AS vector) " +
                   "LIMIT :limit", nativeQuery = true)
    List<Customer> findSimilarCustomers(@Param("embedding") String embedding, @Param("limit") int limit);
    
    // Find customers by profitability score range
    @Query("SELECT c FROM Customer c WHERE c.profitabilityScore BETWEEN :minScore AND :maxScore ORDER BY c.profitabilityScore DESC")
    List<Customer> findByProfitabilityScoreRange(@Param("minScore") java.math.BigDecimal minScore, 
                                                  @Param("maxScore") java.math.BigDecimal maxScore);
    
    // Find customers with high disengagement risk
    @Query("SELECT c FROM Customer c WHERE c.disengagementRiskScore >= :threshold ORDER BY c.disengagementRiskScore DESC")
    List<Customer> findHighRiskCustomers(@Param("threshold") java.math.BigDecimal threshold);
    
    // Find customers with graduation potential
    @Query("SELECT c FROM Customer c WHERE c.graduationPotentialScore >= :threshold ORDER BY c.graduationPotentialScore DESC")
    List<Customer> findGraduationPotentialCustomers(@Param("threshold") java.math.BigDecimal threshold);
}


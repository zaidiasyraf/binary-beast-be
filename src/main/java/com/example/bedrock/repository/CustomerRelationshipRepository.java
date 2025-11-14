package com.example.bedrock.repository;

import com.example.bedrock.entity.CustomerRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRelationshipRepository extends JpaRepository<CustomerRelationship, Long> {
    
    List<CustomerRelationship> findByCustomer1_CustomerId(Long customerId);
    
    List<CustomerRelationship> findByCustomer2_CustomerId(Long customerId);
    
    List<CustomerRelationship> findByRelationshipType(String relationshipType);
    
    // Vector similarity search
    @Query(value = "SELECT cr.*, 1 - (cr.embedding <=> CAST(:embedding AS vector)) AS similarity " +
                   "FROM customer_relationships cr " +
                   "WHERE cr.embedding IS NOT NULL " +
                   "ORDER BY cr.embedding <=> CAST(:embedding AS vector) " +
                   "LIMIT :limit", nativeQuery = true)
    List<CustomerRelationship> findSimilarRelationships(@Param("embedding") String embedding, @Param("limit") int limit);
    
    // Find relationships for a customer (both directions)
    @Query("SELECT cr FROM CustomerRelationship cr WHERE cr.customer1.customerId = :customerId OR cr.customer2.customerId = :customerId")
    List<CustomerRelationship> findAllRelationshipsForCustomer(@Param("customerId") Long customerId);
}


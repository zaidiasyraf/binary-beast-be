package com.example.bedrock.repository;

import com.example.bedrock.entity.DigitalFeatureUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DigitalFeatureUsageRepository extends JpaRepository<DigitalFeatureUsage, Long> {
    
    List<DigitalFeatureUsage> findByCustomer_CustomerId(Long customerId);
    
    List<DigitalFeatureUsage> findByFeatureName(String featureName);
    
    // Find high usage customers for a feature
    @Query("SELECT dfu FROM DigitalFeatureUsage dfu WHERE dfu.featureName = :featureName " +
           "ORDER BY dfu.usageCount DESC")
    List<DigitalFeatureUsage> findByFeatureNameOrderByUsageCountDesc(@Param("featureName") String featureName);
    
    // Find customers with high digital feature scores
    @Query("SELECT dfu FROM DigitalFeatureUsage dfu WHERE dfu.score >= :minScore ORDER BY dfu.score DESC")
    List<DigitalFeatureUsage> findByScoreGreaterThanEqual(@Param("minScore") java.math.BigDecimal minScore);
}


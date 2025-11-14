package com.example.bedrock.repository;

import com.example.bedrock.entity.CustomerProduct;
import com.example.bedrock.entity.CustomerProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerProductRepository extends JpaRepository<CustomerProduct, CustomerProductId> {
    
    List<CustomerProduct> findByCustomer_CustomerId(Long customerId);
    
    List<CustomerProduct> findByProduct_ProductId(Long productId);
    
    List<CustomerProduct> findByStatus(String status);
    
    // Find customers by product type
    @Query("SELECT cp FROM CustomerProduct cp WHERE cp.product.productType = :productType AND cp.status = 'ACTIVE'")
    List<CustomerProduct> findByProductType(@Param("productType") String productType);
    
    // Find products for a customer
    @Query("SELECT cp FROM CustomerProduct cp WHERE cp.customer.customerId = :customerId AND cp.status = 'ACTIVE'")
    List<CustomerProduct> findActiveProductsByCustomerId(@Param("customerId") Long customerId);
}


package com.example.bedrock.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Data
public class CustomerProductId implements Serializable {
    
    private Long customer;
    private Long product;
    
    public CustomerProductId() {}
    
    public CustomerProductId(Long customer, Long product) {
        this.customer = customer;
        this.product = product;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerProductId that = (CustomerProductId) o;
        return Objects.equals(customer, that.customer) &&
               Objects.equals(product, that.product);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(customer, product);
    }
}


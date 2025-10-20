package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.product.Product;
import com.flashdeal.app.domain.product.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 테스트용 H2 데이터베이스 Product Repository
 */
@Repository
public interface TestProductRepository extends JpaRepository<Product, String> {
    
    Optional<Product> findByProductId(String productId);
    
    List<Product> findByStatus(String status);
    
    List<Product> findByStatusAndActiveTrue(String status);
    
    boolean existsByProductId(String productId);
    
    void deleteByProductId(String productId);
}



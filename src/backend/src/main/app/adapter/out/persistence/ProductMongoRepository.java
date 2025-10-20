package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.product.DealStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Product MongoDB Repository
 */
@Repository
public interface ProductMongoRepository extends ReactiveMongoRepository<ProductDocument, String> {
    
    /**
     * 상태별 상품 조회
     */
    Flux<ProductDocument> findByStatus(DealStatus status);
    
    /**
     * 카테고리별 상품 조회
     */
    Flux<ProductDocument> findByCategory(String category);
    
    /**
     * 활성 상품 조회 (ACTIVE 상태)
     */
    default Flux<ProductDocument> findActiveProducts() {
        return findByStatus(DealStatus.ACTIVE);
    }
}






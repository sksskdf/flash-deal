package com.flashdeal.app.adapter.out.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Inventory MongoDB Repository
 */
@Repository
public interface InventoryMongoRepository extends ReactiveMongoRepository<InventoryDocument, String> {
    
    /**
     * 상품 ID로 재고 조회
     */
    Mono<InventoryDocument> findByProductId(String productId);
    
    /**
     * 상품 ID로 재고 존재 여부 확인
     */
    Mono<Boolean> existsByProductId(String productId);
}






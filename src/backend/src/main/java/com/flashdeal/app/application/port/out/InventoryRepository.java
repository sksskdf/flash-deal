package com.flashdeal.app.application.port.out;

import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

/**
 * Inventory Repository Port
 * 
 * Inventory Aggregate의 영속성 관리를 위한 포트 인터페이스
 */
public interface InventoryRepository {
    
    /**
     * 재고 저장
     */
    Mono<Inventory> save(Inventory inventory);
    
    /**
     * ID로 재고 조회
     */
    Mono<Inventory> findById(InventoryId id);
    
    /**
     * 상품 ID로 재고 조회
     */
    Mono<Inventory> findByProductId(ProductId productId);
    
    /**
     * 재고 삭제
     */
    Mono<Void> deleteById(InventoryId id);
    
    /**
     * 재고 존재 여부 확인
     */
    Mono<Boolean> existsByProductId(ProductId productId);
}






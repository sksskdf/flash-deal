package com.flashdeal.app.adapter.out.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Inventory MongoDB Repository
 * 
 * .doc/data/1.model-structure.md의 Inventory 모델을 기반으로 구현
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
    
    /**
     * 재고 레벨별 조회
     */
    Flux<InventoryDocument> findByLevel(String level);
    
    /**
     * 가용 재고가 특정 수량 이상인 재고 조회
     */
    @Query("{ 'stock.available': { $gte: ?0 } }")
    Flux<InventoryDocument> findByAvailableStockGreaterThanEqual(int minAvailable);
    
    /**
     * 가용 재고가 특정 수량 이하인 재고 조회 (품절 임박)
     */
    @Query("{ 'stock.available': { $lte: ?0 } }")
    Flux<InventoryDocument> findByAvailableStockLessThanEqual(int maxAvailable);
    
    /**
     * 안전 재고 이하인 재고 조회
     */
    @Query("{ 'stock.available': { $lte: 'policy.safetyStock' } }")
    Flux<InventoryDocument> findLowStockItems();
    
    /**
     * 재고 레벨과 가용 재고로 조회
     */
    Flux<InventoryDocument> findByLevelAndStockAvailableGreaterThan(String level, int minAvailable);
    
    /**
     * 재입고 정책이 활성화된 재고 조회
     */
    @Query("{ 'policy.restock.enabled': true }")
    Flux<InventoryDocument> findRestockEnabledItems();
    
    /**
     * 재입고 임계값 이하인 재고 조회
     */
    @Query("{ 'stock.available': { $lte: 'policy.restock.threshold' }, 'policy.restock.enabled': true }")
    Flux<InventoryDocument> findItemsNeedingRestock();
    
    /**
     * 특정 상품 ID 목록으로 재고 조회
     */
    Flux<InventoryDocument> findByProductIdIn(Iterable<String> productIds);
    
    /**
     * 재고 이벤트가 있는 재고 조회 (최근 이벤트 기준)
     */
    @Query("{ 'events': { $exists: true, $ne: [] } }")
    Flux<InventoryDocument> findItemsWithEvents();
    
    /**
     * 특정 이벤트 타입이 있는 재고 조회
     */
    @Query("{ 'events.type': ?0 }")
    Flux<InventoryDocument> findByEventType(String eventType);
    
    /**
     * 재고 조정 이력이 있는 재고 조회
     */
    @Query("{ 'adjustments': { $exists: true, $ne: [] } }")
    Flux<InventoryDocument> findItemsWithAdjustments();
    
    /**
     * Redis 동기화 버전으로 재고 조회 (동기화 충돌 감지용)
     */
    @Query("{ 'redis.syncVersion': ?0 }")
    Flux<InventoryDocument> findBySyncVersion(int syncVersion);
    
    /**
     * Redis 마지막 동기화 시간으로 재고 조회
     */
    @Query("{ 'redis.lastSyncedAt': { $lt: ?0 } }")
    Flux<InventoryDocument> findItemsNotSyncedRecently(java.time.ZonedDateTime cutoffTime);
    
    /**
     * 재고 통계 조회용 쿼리
     */
    @Query(value = "{}", fields = "{ 'productId': 1, 'stock': 1, 'level': 1 }")
    Flux<InventoryDocument> findInventorySummary();
    
    /**
     * 특정 상품의 재고 이벤트 조회 (페이지네이션)
     */
    @Query("{ 'productId': ?0, 'events': { $exists: true } }")
    Flux<InventoryDocument> findByProductIdWithEvents(String productId, Pageable pageable);
    
    /**
     * 재고 수량 통계
     */
    Mono<Long> countByLevel(String level);
    
    /**
     * 총 재고 수량 합계
     */
    @Query(value = "{}", fields = "{ 'stock.total': 1 }")
    Flux<InventoryDocument> findAllTotalStock();
}






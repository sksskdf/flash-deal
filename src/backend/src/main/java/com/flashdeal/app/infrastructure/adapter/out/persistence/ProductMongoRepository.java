package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.product.DealStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Product MongoDB Repository
 * 
 * .doc/data/1.model-structure.md의 Product 모델을 기반으로 구현
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
     * 상태와 카테고리로 상품 조회
     */
    Flux<ProductDocument> findByStatusAndCategory(DealStatus status, String category);
    
    /**
     * 활성 상품 조회 (ACTIVE 상태)
     */
    default Flux<ProductDocument> findActiveProducts() {
        return findByStatus(DealStatus.ACTIVE);
    }
    
    /**
     * Featured 상품 조회
     */
    @Query("{ 'metadata.featured': true, 'status': ?0 }")
    Flux<ProductDocument> findFeaturedProducts(DealStatus status);
    
    /**
     * 가격 범위로 상품 조회
     */
    @Query("{ 'price.sale': { $gte: ?0, $lte: ?1 }, 'status': ?2 }")
    Flux<ProductDocument> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, DealStatus status);
    
    /**
     * 할인율로 상품 조회 (높은 할인율 순)
     */
    @Query("{ 'price.rate': { $gte: ?0 }, 'status': ?1 }")
    Flux<ProductDocument> findByDiscountRateGreaterThanEqual(int minDiscountRate, DealStatus status, Pageable pageable);
    
    /**
     * 시작 예정 상품 조회 (현재 시간 기준)
     */
    @Query("{ 'schedule.startsAt': { $lte: ?0 }, 'status': 'UPCOMING' }")
    Flux<ProductDocument> findUpcomingProducts(Instant currentTime);
    
    /**
     * 종료 임박 상품 조회 (현재 시간 기준)
     */
    @Query("{ 'schedule.endsAt': { $lte: ?0 }, 'status': 'ACTIVE' }")
    Flux<ProductDocument> findEndingSoonProducts(Instant currentTime);
    
    @Query("{ 'status': ?0, 'schedule.startsAt': { $lte: ?1 } }")
    Flux<ProductDocument> findByStatusAndScheduleStartAtBefore(DealStatus status, Instant time);

    @Query("{ 'status': ?0, 'schedule.endsAt': { $lte: ?1 } }")
    Flux<ProductDocument> findByStatusAndScheduleEndAtBefore(DealStatus status, Instant time);

    /**
     * 텍스트 검색 (제목, 설명, 카테고리)
     */
    @Query("{ $text: { $search: ?0 }, 'status': ?1 }")
    Flux<ProductDocument> searchByText(String searchText, DealStatus status);
    
    /**
     * 복합 검색 쿼리
     */
    @Query("{ 'status': ?0, 'category': ?1, 'price.sale': { $gte: ?2, $lte: ?3 } }")
    Flux<ProductDocument> findByComplexCriteria(DealStatus status, String category, 
                                               BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * 상품 존재 여부 확인 (상태별)
     */
    Mono<Boolean> existsByStatusAndId(DealStatus status, String id);
    
    /**
     * 카테고리별 상품 수 조회
     */
    Mono<Long> countByCategoryAndStatus(String category, DealStatus status);
    
    /**
     * 상태별 상품 수 조회
     */
    Mono<Long> countByStatus(DealStatus status);
}






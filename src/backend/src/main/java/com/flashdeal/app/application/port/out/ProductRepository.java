package com.flashdeal.app.application.port.out;

import com.flashdeal.app.domain.product.Product;
import com.flashdeal.app.domain.product.ProductFilter;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.domain.product.ProductPage;

import java.util.List;

import com.flashdeal.app.domain.common.Pagination;
import com.flashdeal.app.domain.product.DealStatus;
import com.flashdeal.app.domain.product.ProductSortOption;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Product Repository Port
 * 
 * Product Aggregate의 영속성 관리를 위한 포트 인터페이스
 */
public interface ProductRepository {
    
    /**
     * 상품 저장
     */
    Mono<Product> save(Product product);
    
    /**
     * ID로 상품 조회
     */
    Mono<Product> findById(ProductId id);
    
    /**
     * 상태별 상품 목록 조회
     */
    Flux<Product> findByStatus(DealStatus status);
    
    /**
     * 카테고리별 상품 목록 조회
     */
    Flux<Product> findByCategory(String category);
    
    /**
     * 활성 상품 목록 조회 (진행 중인 딜)
     */
    Flux<Product> findActiveProducts();

    Flux<Product> findByStatusAndScheduleStartAtBefore(DealStatus status, java.time.Instant time);

    Flux<Product> findByStatusAndScheduleEndAtBefore(DealStatus status, java.time.Instant time);
    
    /**
     * 필터링 조회
     */
    Mono<ProductPage> findByFilter(ProductFilter filter, Pagination pagination, List<ProductSortOption> sort);

    /**
     * 상품 삭제
     */
    Mono<Void> deleteById(ProductId id);
    
    /**
     * 상품 존재 여부 확인
     */
    Mono<Boolean> existsById(ProductId id);
}






package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.order.OrderStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Order MongoDB Repository
 */
@Repository
public interface OrderMongoRepository extends ReactiveMongoRepository<OrderDocument, String> {
    
    /**
     * 사용자별 주문 조회
     */
    Flux<OrderDocument> findByUserId(String userId);
    
    /**
     * 상태별 주문 조회
     */
    Flux<OrderDocument> findByStatus(OrderStatus status);
    
    /**
     * 사용자별 상태별 주문 조회
     */
    Flux<OrderDocument> findByUserIdAndStatus(String userId, OrderStatus status);
    
    /**
     * 멱등성 키로 주문 조회
     */
    Mono<OrderDocument> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * 주문 번호로 주문 조회
     */
    Mono<OrderDocument> findByOrderNumber(String orderNumber);
}






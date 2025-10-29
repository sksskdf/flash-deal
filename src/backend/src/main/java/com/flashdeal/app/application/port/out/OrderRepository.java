package com.flashdeal.app.application.port.out;

import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.order.UserId;
import com.flashdeal.app.domain.order.OrderStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Order Repository Port
 * 
 * Order Aggregate의 영속성 관리를 위한 포트 인터페이스
 */
public interface OrderRepository {
    
    /**
     * 주문 저장
     */
    Mono<Order> save(Order order);
    
    /**
     * ID로 주문 조회
     */
    Mono<Order> findById(OrderId id);
    
    /**
     * 사용자별 주문 목록 조회
     */
    Flux<Order> findByUserId(UserId userId);
    
    /**
     * 상태별 주문 목록 조회
     */
    Flux<Order> findByStatus(OrderStatus status);
    
    /**
     * 사용자별 상태별 주문 목록 조회
     */
    Flux<Order> findByUserIdAndStatus(UserId userId, OrderStatus status);
    
    /**
     * 멱등성 키로 주문 조회
     */
    Mono<Order> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * 주문 삭제
     */
    Mono<Void> deleteById(OrderId id);
    
    /**
     * 주문 존재 여부 확인
     */
    Mono<Boolean> existsById(OrderId id);
}






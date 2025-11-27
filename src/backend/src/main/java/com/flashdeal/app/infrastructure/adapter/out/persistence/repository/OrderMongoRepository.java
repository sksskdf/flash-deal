package com.flashdeal.app.infrastructure.adapter.out.persistence.repository;

import com.flashdeal.app.domain.order.OrderStatus;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.OrderDocument;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Order MongoDB Repository
 * 
 * .doc/data/1.model-structure.md의 Order 모델을 기반으로 구현
 */
@Repository
public interface OrderMongoRepository extends ReactiveMongoRepository<OrderDocument, String> {
    
    /**
     * 사용자별 주문 조회
     */
    @Query("{ 'user.id': ?0 }")
    Flux<OrderDocument> findByUserId(String userId);
    
    /**
     * 상태별 주문 조회
     */
    Flux<OrderDocument> findByStatus(OrderStatus status);
    
    /**
     * 사용자별 상태별 주문 조회
     */
    @Query("{ 'user.id': ?0, 'status': ?1 }")
    Flux<OrderDocument> findByUserIdAndStatus(String userId, OrderStatus status);
    
    /**
     * 멱등성 키로 주문 조회 (복수 존재 시 모두 반환)
     */
    Flux<OrderDocument> findAllByIdempotencyKey(String idempotencyKey);
    
    @Query("{ 'status': ?0, 'createdAt': { $lt: ?1 } }")
    Flux<OrderDocument> findByStatusAndCreatedAtBefore(OrderStatus status, Instant time);

    /**
     * 주문 번호로 주문 조회
     */
    Mono<OrderDocument> findByOrderNumber(String orderNumber);
    
    /**
     * 결제 상태별 주문 조회
     */
    @Query("{ 'payment.status': ?0 }")
    Flux<OrderDocument> findByPaymentStatus(String paymentStatus);
    
    /**
     * 사용자별 결제 상태별 주문 조회
     */
    @Query("{ 'user.id': ?0, 'payment.status': ?1 }")
    Flux<OrderDocument> findByUserIdAndPaymentStatus(String userId, String paymentStatus);
    
    /**
     * 특정 상품이 포함된 주문 조회
     */
    @Query("{ 'items.productId': ?0 }")
    Flux<OrderDocument> findByProductId(String productId);
    
    /**
     * 특정 상품과 사용자로 주문 조회
     */
    @Query("{ 'user.id': ?0, 'items.productId': ?1 }")
    Flux<OrderDocument> findByUserIdAndProductId(String userId, String productId);
    
    /**
     * 금액 범위로 주문 조회
     */
    @Query("{ 'pricing.total': { $gte: ?0, $lte: ?1 } }")
    Flux<OrderDocument> findByTotalAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * 특정 기간 내 주문 조회
     */
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    Flux<OrderDocument> findByCreatedAtBetween(Instant startDate, Instant endDate);
    
    /**
     * 사용자별 특정 기간 내 주문 조회
     */
    @Query("{ 'user.id': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    Flux<OrderDocument> findByUserIdAndCreatedAtBetween(String userId, Instant startDate, Instant endDate);
    
    /**
     * 취소된 주문 조회
     */
    @Query("{ 'cancellation.isCancelled': true }")
    Flux<OrderDocument> findCancelledOrders();
    
    /**
     * 사용자별 취소된 주문 조회
     */
    @Query("{ 'user.id': ?0, 'cancellation.isCancelled': true }")
    Flux<OrderDocument> findCancelledOrdersByUserId(String userId);
    
    /**
     * 부분 취소된 주문 조회
     */
    @Query("{ 'cancellation.isPartial': true }")
    Flux<OrderDocument> findPartiallyCancelledOrders();
    
    /**
     * 환불된 주문 조회
     */
    @Query("{ 'payment.refund.amount': { $gt: 0 } }")
    Flux<OrderDocument> findRefundedOrders();
    
    /**
     * 특정 결제 방법으로 주문 조회
     */
    @Query("{ 'payment.method': ?0 }")
    Flux<OrderDocument> findByPaymentMethod(String paymentMethod);
    
    /**
     * 배송 상태별 주문 조회
     */
    @Query("{ 'items.tracking.carrier': ?0 }")
    Flux<OrderDocument> findByShippingCarrier(String carrier);
    
    /**
     * Kafka 이벤트가 있는 주문 조회
     */
    @Query("{ 'kafkaEvents': { $exists: true, $ne: [] } }")
    Flux<OrderDocument> findOrdersWithKafkaEvents();
    
    /**
     * 특정 이벤트 타입이 있는 주문 조회
     */
    @Query("{ 'kafkaEvents.eventType': ?0 }")
    Flux<OrderDocument> findByKafkaEventType(String eventType);
    
    /**
     * 주문 상태 변경 이력이 있는 주문 조회
     */
    @Query("{ 'statusHistory': { $exists: true, $ne: [] } }")
    Flux<OrderDocument> findOrdersWithStatusHistory();
    
    /**
     * 특정 액터가 변경한 주문 조회
     */
    @Query("{ 'statusHistory.actor': ?0 }")
    Flux<OrderDocument> findByStatusHistoryActor(String actor);
    
    /**
     * 메타데이터로 주문 조회 (소스별)
     */
    @Query("{ 'metadata.source': ?0 }")
    Flux<OrderDocument> findBySource(String source);
    
    /**
     * 실험 그룹별 주문 조회
     */
    @Query("{ 'metadata.experiments.checkoutFlow': ?0 }")
    Flux<OrderDocument> findByExperimentGroup(String experimentGroup);
    
    /**
     * 고객 세그먼트별 주문 조회
     */
    @Query("{ 'metadata.customerSegment': ?0 }")
    Flux<OrderDocument> findByCustomerSegment(String customerSegment);
    
    /**
     * 주문 통계 조회용 쿼리
     */
    @Query(value = "{}", fields = "{ 'orderNumber': 1, 'status': 1, 'pricing.total': 1, 'createdAt': 1 }")
    Flux<OrderDocument> findOrderSummary();
    
    /**
     * 페이지네이션을 위한 주문 조회
     */
    Flux<OrderDocument> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    /**
     * 상태별 주문 수 조회
     */
    Mono<Long> countByStatus(OrderStatus status);
    
    /**
     * 사용자별 주문 수 조회
     */
    Mono<Long> countByUserId(String userId);
    
    /**
     * 특정 기간 내 주문 수 조회
     */
    @Query(value = "{ 'createdAt': { $gte: ?0, $lte: ?1 } }", count = true)
    Mono<Long> countByCreatedAtBetween(Instant startDate, Instant endDate);
    
    /**
     * 멱등성 키 존재 여부 확인
     */
    Mono<Boolean> existsByIdempotencyKey(String idempotencyKey);
    
    /**
     * 주문 번호 존재 여부 확인
     */
    Mono<Boolean> existsByOrderNumber(String orderNumber);
}






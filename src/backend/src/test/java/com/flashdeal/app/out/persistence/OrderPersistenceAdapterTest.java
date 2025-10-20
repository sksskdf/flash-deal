package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.order.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
// import org.testcontainers.containers.MongoDBContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Order Persistence Adapter 테스트
 */
@DataMongoTest
// @Testcontainers
class OrderPersistenceAdapterTest {

    // @Container
    // static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Mock MongoDB 설정 (실제 MongoDB 없이 테스트)
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/test");
    }

    @Autowired
    private OrderMongoRepository mongoRepository;

    private OrderPersistenceAdapter adapter;
    private OrderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OrderMapper();
        adapter = new OrderPersistenceAdapter(mongoRepository, mapper);
    }

    @Test
    void shouldSaveAndFindOrder() {
        // Given
        Order order = createTestOrder();

        // When
        Mono<Order> saveResult = adapter.save(order);
        Mono<Order> findResult = saveResult
                .map(Order::getOrderId)
                .flatMap(adapter::findById);

        // Then
        StepVerifier.create(findResult)
                .assertNext(foundOrder -> {
                    assertThat(foundOrder.getOrderId()).isEqualTo(order.getOrderId());
                    assertThat(foundOrder.getOrderNumber()).isEqualTo(order.getOrderNumber());
                    assertThat(foundOrder.getStatus()).isEqualTo(order.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldFindOrdersByUserId() {
        // Given
        Order order1 = createTestOrder();
        Order order2 = createTestOrder();
        UserId userId = order1.getUserId();

        // When
        Flux<Order> saveAll = Flux.just(order1, order2)
                .flatMap(adapter::save);
        
        Flux<Order> findByUserId = saveAll
                .thenMany(adapter.findByUserId(userId));

        // Then
        StepVerifier.create(findByUserId)
                .assertNext(order -> assertThat(order.getUserId()).isEqualTo(userId))
                .assertNext(order -> assertThat(order.getUserId()).isEqualTo(userId))
                .verifyComplete();
    }

    @Test
    void shouldFindOrdersByStatus() {
        // Given
        Order order = createTestOrder();
        order.transitionTo(OrderStatus.PROCESSING);

        // When
        Mono<Order> saveResult = adapter.save(order);
        Flux<Order> findByStatus = saveResult
                .thenMany(adapter.findByStatus(OrderStatus.PROCESSING));

        // Then
        StepVerifier.create(findByStatus)
                .assertNext(foundOrder -> assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING))
                .verifyComplete();
    }

    @Test
    void shouldFindOrderByIdempotencyKey() {
        // Given
        Order order = createTestOrder();
        String idempotencyKey = order.getIdempotencyKey();

        // When
        Mono<Order> saveResult = adapter.save(order);
        Mono<Order> findByIdempotencyKey = saveResult
                .then(adapter.findByIdempotencyKey(idempotencyKey));

        // Then
        StepVerifier.create(findByIdempotencyKey)
                .assertNext(foundOrder -> assertThat(foundOrder.getIdempotencyKey()).isEqualTo(idempotencyKey))
                .verifyComplete();
    }

    @Test
    void shouldDeleteOrder() {
        // Given
        Order order = createTestOrder();
        OrderId orderId = order.getOrderId();

        // When
        Mono<Order> saveResult = adapter.save(order);
        Mono<Void> deleteResult = saveResult
                .then(adapter.deleteById(orderId));
        
        Mono<Boolean> existsResult = deleteResult
                .then(adapter.existsById(orderId));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldCheckOrderExists() {
        // Given
        Order order = createTestOrder();
        OrderId orderId = order.getOrderId();

        // When
        Mono<Order> saveResult = adapter.save(order);
        Mono<Boolean> existsResult = saveResult
                .then(adapter.existsById(orderId));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isTrue())
                .verifyComplete();
    }

    private Order createTestOrder() {
        OrderId orderId = OrderId.generate();
        UserId userId = UserId.generate();
        String orderNumber = "ORD-" + System.currentTimeMillis();
        String idempotencyKey = "idempotency-" + System.currentTimeMillis();

        // 임시로 빈 리스트와 기본 Shipping으로 생성
        List<OrderItem> items = new ArrayList<>();
        Shipping shipping = new Shipping("STANDARD", 
            new Recipient("Test User", "010-0000-0000"),
            new Address("Test Street", "Test City", "00000", "KR"),
            "Test instructions");

        return new Order(orderId, userId, items, shipping);
    }
}

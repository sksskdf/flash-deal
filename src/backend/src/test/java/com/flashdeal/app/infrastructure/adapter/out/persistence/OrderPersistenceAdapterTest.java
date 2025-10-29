package com.flashdeal.app.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.flashdeal.app.TestDataFactory;
import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.order.OrderStatus;
import com.flashdeal.app.domain.order.UserId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Order Persistence Adapter 테스트
 */
@DataMongoTest
@Testcontainers
@ActiveProfiles("test")
class OrderPersistenceAdapterTest {

        @Container
        public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(
                        DockerImageName.parse("mongo:4.4.2"))
                        .withReuse(true)
                        .withStartupTimeout(Duration.ofSeconds(60))
                        .waitingFor(Wait.forLogMessage(".*Waiting for connections.*", 1)
                                        .withStartupTimeout(Duration.ofSeconds(60)));

        @DynamicPropertySource
        static void setProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
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
                Order order = TestDataFactory.createOrder();

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
                Order order1 = TestDataFactory.createOrder();
                UserId userId = order1.getUserId();
                Order order2 = TestDataFactory.createOrder(OrderId.generate(), userId);

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
                Order order = TestDataFactory.createOrder();
                order.transitionTo(OrderStatus.PROCESSING);

                // When
                Mono<Order> saveResult = adapter.save(order);
                Flux<Order> findByStatus = saveResult
                                .thenMany(adapter.findByStatus(OrderStatus.PROCESSING));

                // Then
                StepVerifier.create(findByStatus)
                                .assertNext(foundOrder -> assertThat(foundOrder.getStatus())
                                                .isEqualTo(OrderStatus.PROCESSING))
                                .verifyComplete();
        }

        @Test
        void shouldFindOrderByIdempotencyKey() {
                // Given
                Order order = TestDataFactory.createOrder();
                String idempotencyKey = order.getIdempotencyKey();

                // When
                Mono<Order> saveResult = adapter.save(order);
                Mono<Order> findByIdempotencyKey = saveResult
                                .then(adapter.findByIdempotencyKey(idempotencyKey));

                // Then
                StepVerifier.create(findByIdempotencyKey)
                                .assertNext(foundOrder -> assertThat(foundOrder.getIdempotencyKey())
                                                .isEqualTo(idempotencyKey))
                                .verifyComplete();
        }

        @Test
        void shouldDeleteOrder() {
                // Given
                Order order = TestDataFactory.createOrder();
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
                Order order = TestDataFactory.createOrder();
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
}

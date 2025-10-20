package com.flashdeal.app.adapter.out.cache;

import com.flashdeal.app.domain.product.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
// import org.testcontainers.containers.GenericContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Redis Cache Adapter 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
// @Testcontainers
class RedisCacheAdapterTest {

    // @Container
    // static GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
    //         .withExposedPorts(6379);

    // @DynamicPropertySource는 application-test.yml에서 처리

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    private RedisCacheAdapter adapter;
    private ProductId productId;

    @BeforeEach
    void setUp() {
        adapter = new RedisCacheAdapter(redisTemplate);
        productId = ProductId.generate();
    }

    @Test
    void shouldSetAndGetInventory() {
        // Given
        int quantity = 100;

        // When
        Mono<Boolean> setResult = adapter.setInventory(productId, quantity);
        Mono<Integer> getResult = setResult.then(adapter.getInventory(productId));

        // Then
        StepVerifier.create(getResult)
                .assertNext(retrievedQuantity -> assertThat(retrievedQuantity).isEqualTo(quantity))
                .verifyComplete();
    }

    @Test
    void shouldDecrementInventory() {
        // Given
        int initialQuantity = 100;
        int decrementAmount = 20;

        // When
        Mono<Boolean> setResult = adapter.setInventory(productId, initialQuantity);
        Mono<Long> decrementResult = setResult.then(adapter.decrementInventory(productId, decrementAmount));
        Mono<Integer> getResult = decrementResult.then(adapter.getInventory(productId));

        // Then
        StepVerifier.create(getResult)
                .assertNext(finalQuantity -> assertThat(finalQuantity).isEqualTo(initialQuantity - decrementAmount))
                .verifyComplete();
    }

    @Test
    void shouldIncrementInventory() {
        // Given
        int initialQuantity = 100;
        int incrementAmount = 20;

        // When
        Mono<Boolean> setResult = adapter.setInventory(productId, initialQuantity);
        Mono<Long> incrementResult = setResult.then(adapter.incrementInventory(productId, incrementAmount));
        Mono<Integer> getResult = incrementResult.then(adapter.getInventory(productId));

        // Then
        StepVerifier.create(getResult)
                .assertNext(finalQuantity -> assertThat(finalQuantity).isEqualTo(initialQuantity + incrementAmount))
                .verifyComplete();
    }

    @Test
    void shouldSetAndRemoveReservation() {
        // Given
        String orderId = "order-123";
        int quantity = 5;

        // When
        Mono<Boolean> setReservationResult = adapter.setReservation(productId, orderId, quantity);
        Mono<Long> removeReservationResult = setReservationResult
                .then(adapter.removeReservation(productId, orderId));

        // Then
        StepVerifier.create(removeReservationResult)
                .assertNext(removedCount -> assertThat(removedCount).isEqualTo(1))
                .verifyComplete();
    }

    @Test
    void shouldGetReservationCount() {
        // Given
        String orderId1 = "order-123";
        String orderId2 = "order-456";
        int quantity = 5;

        // When
        Mono<Boolean> setReservation1 = adapter.setReservation(productId, orderId1, quantity);
        Mono<Boolean> setReservation2 = setReservation1.then(adapter.setReservation(productId, orderId2, quantity));
        Mono<Long> reservationCount = setReservation2.then(adapter.getReservationCount(productId));

        // Then
        StepVerifier.create(reservationCount)
                .assertNext(count -> assertThat(count).isEqualTo(2))
                .verifyComplete();
    }

    @Test
    void shouldDeleteInventory() {
        // Given
        int quantity = 100;

        // When
        Mono<Boolean> setResult = adapter.setInventory(productId, quantity);
        Mono<Boolean> deleteResult = setResult.then(adapter.deleteInventory(productId));
        Mono<Boolean> existsResult = deleteResult.then(adapter.existsInventory(productId));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldCheckInventoryExists() {
        // Given
        int quantity = 100;

        // When
        Mono<Boolean> setResult = adapter.setInventory(productId, quantity);
        Mono<Boolean> existsResult = setResult.then(adapter.existsInventory(productId));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldSetAndGetInventoryTTL() {
        // Given
        int quantity = 100;
        Duration ttl = Duration.ofMinutes(5);

        // When
        Mono<Boolean> setResult = adapter.setInventory(productId, quantity);
        Mono<Boolean> setTTLResult = setResult.then(adapter.setInventoryTTL(productId, ttl));
        Mono<Duration> getTTLResult = setTTLResult.then(adapter.getInventoryTTL(productId));

        // Then
        StepVerifier.create(getTTLResult)
                .assertNext(retrievedTTL -> assertThat(retrievedTTL).isGreaterThan(Duration.ZERO))
                .verifyComplete();
    }
}


package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.inventory.*;
import com.flashdeal.app.domain.product.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
// import org.testcontainers.containers.MongoDBContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Inventory Persistence Adapter 테스트
 */
@DataMongoTest
// @Testcontainers
class InventoryPersistenceAdapterTest {

    // @Container
    // static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Mock MongoDB 설정 (실제 MongoDB 없이 테스트)
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/test");
    }

    @Autowired
    private InventoryMongoRepository mongoRepository;

    private InventoryPersistenceAdapter adapter;
    private InventoryMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InventoryMapper();
        adapter = new InventoryPersistenceAdapter(mongoRepository, mapper);
    }

    @Test
    void shouldSaveAndFindInventory() {
        // Given
        Inventory inventory = createTestInventory();

        // When
        Mono<Inventory> saveResult = adapter.save(inventory);
        Mono<Inventory> findResult = saveResult
                .map(Inventory::getInventoryId)
                .flatMap(adapter::findById);

        // Then
        StepVerifier.create(findResult)
                .assertNext(foundInventory -> {
                    assertThat(foundInventory.getInventoryId()).isEqualTo(inventory.getInventoryId());
                    assertThat(foundInventory.getProductId()).isEqualTo(inventory.getProductId());
                    assertThat(foundInventory.getStock().getTotal()).isEqualTo(inventory.getStock().getTotal());
                    assertThat(foundInventory.getStock().getAvailable()).isEqualTo(inventory.getStock().getAvailable());
                })
                .verifyComplete();
    }

    @Test
    void shouldFindInventoryByProductId() {
        // Given
        Inventory inventory = createTestInventory();
        ProductId productId = inventory.getProductId();

        // When
        Mono<Inventory> saveResult = adapter.save(inventory);
        Mono<Inventory> findByProductIdResult = saveResult
                .then(adapter.findByProductId(productId));

        // Then
        StepVerifier.create(findByProductIdResult)
                .assertNext(foundInventory -> {
                    assertThat(foundInventory.getProductId()).isEqualTo(productId);
                    assertThat(foundInventory.getStock().getTotal()).isEqualTo(1000);
                })
                .verifyComplete();
    }

    @Test
    void shouldDeleteInventory() {
        // Given
        Inventory inventory = createTestInventory();
        InventoryId inventoryId = inventory.getInventoryId();

        // When
        Mono<Inventory> saveResult = adapter.save(inventory);
        Mono<Void> deleteResult = saveResult
                .then(adapter.deleteById(inventoryId));
        
        Mono<Boolean> existsResult = deleteResult
                .then(adapter.existsByProductId(inventory.getProductId()));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldCheckInventoryExistsByProductId() {
        // Given
        Inventory inventory = createTestInventory();
        ProductId productId = inventory.getProductId();

        // When
        Mono<Inventory> saveResult = adapter.save(inventory);
        Mono<Boolean> existsResult = saveResult
                .then(adapter.existsByProductId(productId));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isTrue())
                .verifyComplete();
    }

    @Test
    void shouldReserveInventory() {
        // Given
        Inventory inventory = createTestInventory();
        int reserveQuantity = 5;

        // When
        Mono<Inventory> saveResult = adapter.save(inventory);
        Mono<Inventory> reserveResult = saveResult
                .map(inv -> {
                    inv.reserve(reserveQuantity);
                    return inv;
                })
                .flatMap(adapter::save);

        // Then
        StepVerifier.create(reserveResult)
                .assertNext(reservedInventory -> {
                    assertThat(reservedInventory.getStock().getAvailable()).isEqualTo(995); // 1000 - 5
                    assertThat(reservedInventory.getStock().getReserved()).isEqualTo(5);
                    assertThat(reservedInventory.getStock().getTotal()).isEqualTo(1000);
                })
                .verifyComplete();
    }

    private Inventory createTestInventory() {
        InventoryId inventoryId = InventoryId.generate();
        ProductId productId = ProductId.generate();
        Stock stock = Stock.initial(1000);
        Policy policy = Policy.defaultPolicy();

        return new Inventory(inventoryId, productId, stock, policy);
    }
}




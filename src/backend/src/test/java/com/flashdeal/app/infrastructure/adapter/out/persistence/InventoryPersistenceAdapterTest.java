package com.flashdeal.app.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.infrastructure.adapter.out.persistence.mapper.InventoryMapper;
import com.flashdeal.app.infrastructure.adapter.out.persistence.repository.InventoryMongoRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Inventory Persistence Adapter 테스트
 */
@DataMongoTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Inventory Persistence Adapter 테스트")
class InventoryPersistenceAdapterTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"))
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(60))
            .waitingFor(Wait.forLogMessage(".*Waiting for connections.*", 1)
                    .withStartupTimeout(Duration.ofSeconds(60)));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
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
    @DisplayName("재고를 저장하고 조회할 수 있다")
    void shouldSaveAndFindInventory() {
        // Given
        Inventory inventory = TestDataFactory.createInventory();

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
    @DisplayName("상품 ID로 재고를 조회할 수 있다")
    void shouldFindInventoryByProductId() {
        // Given
        Inventory inventory = TestDataFactory.createInventory();
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
    @DisplayName("재고를 삭제할 수 있다")
    void shouldDeleteInventory() {
        // Given
        Inventory inventory = TestDataFactory.createInventory();
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
    @DisplayName("상품 ID로 재고 존재 여부를 확인할 수 있다")
    void shouldCheckInventoryExistsByProductId() {
        // Given
        Inventory inventory = TestDataFactory.createInventory();
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
    @DisplayName("재고를 예약할 수 있다")
    void shouldReserveInventory() {
        // Given
        Inventory inventory = TestDataFactory.createInventory();
        int reserveQuantity = 5;

        // When
        Mono<Inventory> saveResult = adapter.save(inventory);
        Mono<Inventory> reserveResult = saveResult
                .map(inv -> inv.reserve(reserveQuantity))
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
}
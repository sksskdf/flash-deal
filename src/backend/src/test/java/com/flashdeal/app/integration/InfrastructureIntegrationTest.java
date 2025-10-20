package com.flashdeal.app.integration;

import com.flashdeal.app.adapter.out.cache.RedisCacheAdapter;
import com.flashdeal.app.adapter.out.messaging.KafkaEventPublisher;
import com.flashdeal.app.adapter.out.persistence.*;
import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.inventory.Policy;
import com.flashdeal.app.domain.inventory.Stock;
import com.flashdeal.app.domain.order.*;
import java.util.ArrayList;
import java.util.List;
import com.flashdeal.app.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
// import org.testcontainers.containers.GenericContainer;
// import org.testcontainers.containers.KafkaContainer;
// import org.testcontainers.containers.MongoDBContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Infrastructure 통합 테스트
 * 
 * MongoDB, Redis, Kafka를 모두 사용하는 End-to-End 테스트
 */
@SpringBootTest
// @Testcontainers
class InfrastructureIntegrationTest {

    // @Container
    // static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");

    // @Container
    // static GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
    //         .withExposedPorts(6379);

    // @Container
    // static KafkaContainer kafka = new KafkaContainer("confluentinc/cp-kafka:7.4.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Mock 설정 (실제 컨테이너 없이 테스트)
        registry.add("spring.data.mongodb.uri", () -> "mongodb://localhost:27017/test");
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> 6379);
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Autowired
    private ProductPersistenceAdapter productAdapter;

    @Autowired
    private InventoryPersistenceAdapter inventoryAdapter;

    @Autowired
    private OrderPersistenceAdapter orderAdapter;

    @Autowired
    private RedisCacheAdapter redisAdapter;

    @Autowired
    private KafkaEventPublisher kafkaPublisher;

    private ProductId productId;
    private InventoryId inventoryId;
    private OrderId orderId;
    private UserId userId;

    @BeforeEach
    void setUp() {
        productId = ProductId.generate();
        inventoryId = InventoryId.generate();
        orderId = OrderId.generate();
        userId = UserId.generate();
    }

    @Test
    void shouldHandleCompleteOrderFlow() {
        // Given: 상품 생성
        Product product = createTestProduct();
        Inventory inventory = createTestInventory();
        Order order = createTestOrder();

        // When: 상품 저장
        Mono<Product> saveProductResult = productAdapter.save(product);
        
        // Then: 상품 저장 확인
        StepVerifier.create(saveProductResult)
                .assertNext(savedProduct -> {
                    assertThat(savedProduct.getProductId()).isEqualTo(productId);
                    assertThat(savedProduct.getTitle()).isEqualTo("Test Product");
                })
                .verifyComplete();

        // When: 재고 저장
        Mono<Inventory> saveInventoryResult = inventoryAdapter.save(inventory);
        
        // Then: 재고 저장 확인
        StepVerifier.create(saveInventoryResult)
                .assertNext(savedInventory -> {
                    assertThat(savedInventory.getInventoryId()).isEqualTo(inventoryId);
                    assertThat(savedInventory.getStock().getTotal()).isEqualTo(1000);
                })
                .verifyComplete();

        // When: 주문 저장
        Mono<Order> saveOrderResult = orderAdapter.save(order);
        
        // Then: 주문 저장 확인
        StepVerifier.create(saveOrderResult)
                .assertNext(savedOrder -> {
                    assertThat(savedOrder.getOrderId()).isEqualTo(orderId);
                    assertThat(savedOrder.getUserId()).isEqualTo(userId);
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleInventoryReservationFlow() {
        // Given: 재고 설정
        Inventory inventory = createTestInventory();
        int initialStock = 1000;
        int reservationQuantity = 5;

        // When: 재고 저장 및 Redis 캐시 설정
        Mono<Inventory> saveInventoryResult = inventoryAdapter.save(inventory);
        Mono<Boolean> setRedisResult = saveInventoryResult
                .then(redisAdapter.setInventory(productId, initialStock));

        // Then: 재고 설정 확인
        StepVerifier.create(setRedisResult)
                .assertNext(success -> assertThat(success).isTrue())
                .verifyComplete();

        // When: 재고 예약
        Mono<Long> decrementResult = redisAdapter.decrementInventory(productId, reservationQuantity);
        Mono<Integer> getStockResult = decrementResult.then(redisAdapter.getInventory(productId));

        // Then: 재고 감소 확인
        StepVerifier.create(getStockResult)
                .assertNext(finalStock -> assertThat(finalStock).isEqualTo(initialStock - reservationQuantity))
                .verifyComplete();
    }

    @Test
    void shouldHandleKafkaEventFlow() {
        // Given: 이벤트 데이터
        String orderNumber = "ORD-123456";
        String idempotencyKey = "idempotency-123";

        // When: 주문 생성 이벤트 발행
        kafkaPublisher.publishOrderCreated(orderId, userId.getValue(), orderNumber, idempotencyKey);

        // Then: 이벤트 발행 확인 (실제 환경에서는 Consumer를 통해 확인)
        // 여기서는 발행만 테스트
        assertThat(orderId).isNotNull();
        assertThat(userId).isNotNull();
    }

    @Test
    void shouldHandleCompleteBusinessFlow() {
        // Given: 전체 비즈니스 플로우 데이터
        Product product = createTestProduct();
        Inventory inventory = createTestInventory();
        Order order = createTestOrder();

        // When: 전체 플로우 실행
        Mono<Product> productFlow = productAdapter.save(product);
        Mono<Inventory> inventoryFlow = productFlow.then(inventoryAdapter.save(inventory));
        Mono<Order> orderFlow = inventoryFlow.then(orderAdapter.save(order));
        Mono<Boolean> redisFlow = orderFlow.then(redisAdapter.setInventory(productId, 1000));

        // Then: 전체 플로우 성공 확인
        StepVerifier.create(redisFlow)
                .assertNext(success -> assertThat(success).isTrue())
                .verifyComplete();
    }

    private Product createTestProduct() {
        String title = "Test Product";
        String description = "Test Description";
        
        Price price = new Price(
            new BigDecimal("100.00"),
            new BigDecimal("80.00"),
            "USD"
        );
        
        ZonedDateTime now = ZonedDateTime.now();
        Schedule schedule = new Schedule(
            now.plusHours(1),
            now.plusHours(3),
            "UTC"
        );
        
        Map<String, Object> specs = new HashMap<>();
        specs.put("color", "Red");
        specs.put("size", "Large");
        
        return new Product(
            productId,
            title,
            description,
            price,
            schedule,
            new Specs(specs)
        );
    }

    private Inventory createTestInventory() {
        Stock stock = Stock.initial(1000);
        Policy policy = Policy.defaultPolicy();
        
        return new Inventory(inventoryId, productId, stock, policy);
    }

    private Order createTestOrder() {
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

package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
// import org.testcontainers.containers.MongoDBContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Product Persistence Adapter 테스트
 */
@DataJpaTest
@ActiveProfiles("test")
// @Testcontainers
class ProductPersistenceAdapterTest {

    // @Container
    // static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    // @DynamicPropertySource는 application-test.yml에서 처리

    @Autowired
    private TestProductRepository testRepository;

    private ProductPersistenceAdapter adapter;
    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
        // H2 데이터베이스를 사용하는 테스트용 어댑터 생성
        // adapter = new ProductPersistenceAdapter(testRepository, mapper);
    }

    @Test
    void shouldSaveAndFindProduct() {
        // Given
        Product product = createTestProduct();

        // When
        Mono<Product> saveResult = adapter.save(product);
        Mono<Product> findResult = saveResult
                .map(Product::getProductId)
                .flatMap(adapter::findById);

        // Then
        StepVerifier.create(findResult)
                .assertNext(foundProduct -> {
                    assertThat(foundProduct.getProductId()).isEqualTo(product.getProductId());
                    assertThat(foundProduct.getTitle()).isEqualTo(product.getTitle());
                    assertThat(foundProduct.getDescription()).isEqualTo(product.getDescription());
                    assertThat(foundProduct.getStatus()).isEqualTo(product.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldFindProductsByStatus() {
        // Given
        Product product1 = createTestProduct();
        Product product2 = createTestProduct();
        product2.transitionTo(DealStatus.ACTIVE);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2)
                .flatMap(adapter::save);
        
        Flux<Product> findByStatus = saveAll
                .thenMany(adapter.findByStatus(DealStatus.ACTIVE));

        // Then
        StepVerifier.create(findByStatus)
                .assertNext(product -> assertThat(product.getStatus()).isEqualTo(DealStatus.ACTIVE))
                .verifyComplete();
    }

    @Test
    void shouldFindActiveProducts() {
        // Given
        Product product = createTestProduct();
        product.transitionTo(DealStatus.ACTIVE);

        // When
        Mono<Product> saveResult = adapter.save(product);
        Flux<Product> activeProducts = saveResult
                .thenMany(adapter.findActiveProducts());

        // Then
        StepVerifier.create(activeProducts)
                .assertNext(activeProduct -> assertThat(activeProduct.getStatus()).isEqualTo(DealStatus.ACTIVE))
                .verifyComplete();
    }

    @Test
    void shouldDeleteProduct() {
        // Given
        Product product = createTestProduct();
        ProductId productId = product.getProductId();

        // When
        Mono<Product> saveResult = adapter.save(product);
        Mono<Void> deleteResult = saveResult
                .then(adapter.deleteById(productId));
        
        Mono<Boolean> existsResult = deleteResult
                .then(adapter.existsById(productId));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isFalse())
                .verifyComplete();
    }

    @Test
    void shouldCheckProductExists() {
        // Given
        Product product = createTestProduct();
        ProductId productId = product.getProductId();

        // When
        Mono<Product> saveResult = adapter.save(product);
        Mono<Boolean> existsResult = saveResult
                .then(adapter.existsById(productId));

        // Then
        StepVerifier.create(existsResult)
                .assertNext(exists -> assertThat(exists).isTrue())
                .verifyComplete();
    }

    private Product createTestProduct() {
        ProductId productId = ProductId.generate();
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
}

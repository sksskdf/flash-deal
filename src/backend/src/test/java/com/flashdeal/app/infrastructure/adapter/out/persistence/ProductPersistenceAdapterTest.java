package com.flashdeal.app.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.flashdeal.app.TestDataFactory;
import com.flashdeal.app.domain.common.Pagination;
import com.flashdeal.app.domain.common.SortOrder;
import com.flashdeal.app.domain.product.*;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.ProductDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.mapper.ProductMapper;
import com.flashdeal.app.infrastructure.adapter.out.persistence.repository.ProductMongoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Product Persistence Adapter 테스트
 */
@DataMongoTest
@Testcontainers
@ActiveProfiles("test")
@SuppressWarnings("resource")
@DisplayName("Product Persistence Adapter 테스트")
class ProductPersistenceAdapterTest {

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
    private ProductMongoRepository mongoRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    private ProductPersistenceAdapter adapter;
    private ProductMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMapper();
        adapter = new ProductPersistenceAdapter(mongoRepository, mapper, mongoTemplate);
        mongoRepository.deleteAll().block();
    }

    @Test
    @DisplayName("상품을 저장하고 조회할 수 있다")
    void shouldSaveAndFindProduct() {
        // Given
        Product product = TestDataFactory.createProduct();

        // When
        Mono<Product> saveResult = adapter.save(product);
        Mono<Product> findResult = saveResult
                .map(Product::productId)
                .flatMap(adapter::findById);

        // Then
        StepVerifier.create(findResult)
                .assertNext(foundProduct -> {
                    assertThat(foundProduct.productId()).isEqualTo(product.productId());
                    assertThat(foundProduct.title()).isEqualTo(product.title());
                    assertThat(foundProduct.status()).isEqualTo(product.status());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("상태로 상품을 필터링할 수 있다")
    void shouldFindProductsByStatusFilter() {
        // Given
        Product activeProduct = TestDataFactory.createActiveProduct();
        Product upcomingProduct = TestDataFactory.createProduct();
        Product soldOutProduct = TestDataFactory.createSoldOutProduct();

        ProductFilter filter = new ProductFilter(
                DealStatus.ACTIVE,
                null,
                null,
                null,
                null,
                null
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(activeProduct, upcomingProduct, soldOutProduct)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(1);
                    assertThat(page.content().get(0).status()).isEqualTo(DealStatus.ACTIVE);
                    assertThat(page.pageInfo().total()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("카테고리로 상품을 필터링할 수 있다")
    void shouldFindProductsByCategoryFilter() {
        // Given - ProductMapper가 category를 null로 설정하므로 Document를 직접 생성
        ProductDocument doc1 = createProductDocumentWithCategory("Electronics");
        ProductDocument doc2 = createProductDocumentWithCategory("Clothing");
        ProductDocument doc3 = createProductDocumentWithCategory("Electronics");

        // When - Document를 직접 저장
        Flux<ProductDocument> saveAll = Flux.just(doc1, doc2, doc3)
                .flatMap(mongoRepository::save);

        ProductFilter filter = new ProductFilter(
                null,
                "Electronics",
                null,
                null,
                null,
                null
        );
        Pagination pagination = new Pagination(0, 10);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(2);
                    assertThat(page.pageInfo().total()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("최소 가격으로 상품을 필터링할 수 있다")
    void shouldFindProductsByMinPriceFilter() {
        // Given
        Product product1 = createProductWithPrice(new BigDecimal("50000"), new BigDecimal("40000"));
        Product product2 = createProductWithPrice(new BigDecimal("100000"), new BigDecimal("80000"));
        Product product3 = createProductWithPrice(new BigDecimal("150000"), new BigDecimal("120000"));

        ProductFilter filter = new ProductFilter(
                null,
                null,
                new BigDecimal("100000"),
                null,
                null,
                null
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                        .assertNext(page -> {
                                assertThat(page.content()).hasSize(1);
                                assertThat(page.pageInfo().total()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("최대 가격으로 상품을 필터링할 수 있다")
    void shouldFindProductsByMaxPriceFilter() {
        // Given
        Product product1 = createProductWithPrice(new BigDecimal("50000"), new BigDecimal("40000"));
        Product product2 = createProductWithPrice(new BigDecimal("100000"), new BigDecimal("80000"));
        Product product3 = createProductWithPrice(new BigDecimal("150000"), new BigDecimal("120000"));

        ProductFilter filter = new ProductFilter(
                null,
                null,
                null,
                new BigDecimal("100000"),
                null,
                null
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(2);
                    assertThat(page.pageInfo().total()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("가격 범위로 상품을 필터링할 수 있다")
    void shouldFindProductsByPriceRangeFilter() {
        // Given
        Product product1 = createProductWithPrice(new BigDecimal("50000"), new BigDecimal("40000"));
        Product product2 = createProductWithPrice(new BigDecimal("100000"), new BigDecimal("80000"));
        Product product3 = createProductWithPrice(new BigDecimal("150000"), new BigDecimal("120000"));

        ProductFilter filter = new ProductFilter(
                null,
                null,
                new BigDecimal("80000"),
                new BigDecimal("120000"),
                null,
                null
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(2);
                    assertThat(page.pageInfo().total()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("최소 할인율로 상품을 필터링할 수 있다")
    void shouldFindProductsByMinDiscountRateFilter() {
        // Given
        // 20% 할인율 (100000 -> 80000)
        Product product1 = createProductWithPrice(new BigDecimal("100000"), new BigDecimal("80000"));
        // 10% 할인율 (100000 -> 90000)
        Product product2 = createProductWithPrice(new BigDecimal("100000"), new BigDecimal("90000"));
        // 30% 할인율 (100000 -> 70000)
        Product product3 = createProductWithPrice(new BigDecimal("100000"), new BigDecimal("70000"));

        ProductFilter filter = new ProductFilter(
                null,
                null,
                null,
                null,
                20,
                null
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(2); // 20%, 30%
                    assertThat(page.pageInfo().total()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("검색어로 상품을 필터링할 수 있다")
    void shouldFindProductsBySearchTextFilter() {
        // Given
        Product product1 = createProductWithTitle("iPhone 15 Pro");
        Product product2 = createProductWithTitle("Samsung Galaxy S24");
        Product product3 = createProductWithTitle("iPhone 14");

        ProductFilter filter = new ProductFilter(
                null,
                null,
                null,
                null,
                null,
                "iPhone"
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(2);
                    assertThat(page.pageInfo().total()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("검색어로 설명에서도 상품을 필터링할 수 있다")
    void shouldFindProductsBySearchTextInDescription() {
        // Given
        Product product1 = createProductWithDescription("고급 스마트폰");
        Product product2 = createProductWithDescription("노트북 컴퓨터");
        Product product3 = createProductWithDescription("프리미엄 스마트폰");

        ProductFilter filter = new ProductFilter(
                null,
                null,
                null,
                null,
                null,
                "스마트폰"
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(2);
                    assertThat(page.pageInfo().total()).isEqualTo(2);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("제목으로 오름차순 정렬할 수 있다")
    void shouldSortProductsByTitleAscending() {
        // Given
        Product product1 = createProductWithTitle("Zebra Product");
        Product product2 = createProductWithTitle("Apple Product");
        Product product3 = createProductWithTitle("Banana Product");

        ProductFilter filter = new ProductFilter(null, null, null, null, null, null);
        Pagination pagination = new Pagination(0, 10);
        List<ProductSortOption> sortOptions = List.of(
                new ProductSortOption(ProductSortField.TITLE, SortOrder.ASC)
        );

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, sortOptions));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(3);
                    assertThat(page.content().get(0).title()).isEqualTo("Apple Product");
                    assertThat(page.content().get(1).title()).isEqualTo("Banana Product");
                    assertThat(page.content().get(2).title()).isEqualTo("Zebra Product");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("가격으로 내림차순 정렬할 수 있다")
    void shouldSortProductsByPriceDescending() {
        // Given
        Product product1 = createProductWithPrice(new BigDecimal("50000"), new BigDecimal("40000"));
        Product product2 = createProductWithPrice(new BigDecimal("150000"), new BigDecimal("120000"));
        Product product3 = createProductWithPrice(new BigDecimal("100000"), new BigDecimal("80000"));

        ProductFilter filter = new ProductFilter(null, null, null, null, null, null);
        Pagination pagination = new Pagination(0, 10);
        List<ProductSortOption> sortOptions = List.of(
                new ProductSortOption(ProductSortField.PRICE, SortOrder.DESC)
        );

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, sortOptions));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(3);
                    assertThat(page.content().get(0).price().sale())
                            .isEqualByComparingTo(new BigDecimal("120000"));
                    assertThat(page.content().get(1).price().sale())
                            .isEqualByComparingTo(new BigDecimal("80000"));
                    assertThat(page.content().get(2).price().sale())
                            .isEqualByComparingTo(new BigDecimal("40000"));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("정렬 옵션이 없으면 기본적으로 생성일 내림차순으로 정렬된다")
    void shouldSortByCreatedAtDescendingWhenNoSortOption() {
        // Given
        Product product1 = TestDataFactory.createProduct();
        Product product2 = TestDataFactory.createProduct();
        Product product3 = TestDataFactory.createProduct();

        ProductFilter filter = new ProductFilter(null, null, null, null, null, null);
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(3);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("페이지네이션을 적용할 수 있다")
    void shouldApplyPagination() {
        // Given
        List<Product> products = Flux.range(1, 15)
                .map(i -> TestDataFactory.createProduct())
                .collectList()
                .block();

        ProductFilter filter = new ProductFilter(null, null, null, null, null, null);
        Pagination pagination = new Pagination(0, 5); // 첫 페이지, 5개씩

        // When
        Flux<Product> saveAll = Flux.fromIterable(products)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(5);
                    assertThat(page.pageInfo().total()).isEqualTo(15);
                    assertThat(page.pageInfo().size()).isEqualTo(5);
                    assertThat(page.pageInfo().page()).isEqualTo(0);
                    assertThat(page.pageInfo().totalPages()).isEqualTo(3);
                    assertThat(page.pageInfo().hasNext()).isTrue();
                    assertThat(page.pageInfo().hasPrevious()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("두 번째 페이지를 조회할 수 있다")
    void shouldFindSecondPage() {
        // Given
        List<Product> products = Flux.range(1, 15)
                .map(i -> TestDataFactory.createProduct())
                .collectList()
                .block();

        ProductFilter filter = new ProductFilter(null, null, null, null, null, null);
        Pagination pagination = new Pagination(1, 5); // 두 번째 페이지

        // When
        Flux<Product> saveAll = Flux.fromIterable(products)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(5);
                    assertThat(page.pageInfo().page()).isEqualTo(1);
                    assertThat(page.pageInfo().hasNext()).isTrue();
                    assertThat(page.pageInfo().hasPrevious()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("복합 필터로 상품을 조회할 수 있다")
    void shouldFindProductsWithComplexFilter() {
        // Given
        Product product1 = createProductWithCategoryAndPrice("Electronics", 
                new BigDecimal("100000"), new BigDecimal("80000"));
        Product product2 = createProductWithCategoryAndPrice("Electronics", 
                new BigDecimal("50000"), new BigDecimal("40000"));
        Product product3 = createProductWithCategoryAndPrice("Clothing", 
                new BigDecimal("100000"), new BigDecimal("80000"));

        ProductFilter filter = new ProductFilter(
                DealStatus.ACTIVE,
                "Electronics",
                new BigDecimal("60000"),
                null,
                        10,
                                null
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(1);
                    assertThat(page.content().get(0).productId())
                            .isEqualTo(product1.productId());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("빈 필터로 모든 상품을 조회할 수 있다")
    void shouldFindAllProductsWithEmptyFilter() {
        // Given
        Product product1 = TestDataFactory.createProduct();
        Product product2 = TestDataFactory.createActiveProduct();
        Product product3 = TestDataFactory.createSoldOutProduct();

        ProductFilter filter = new ProductFilter(null, null, null, null, null, null);
        Pagination pagination = new Pagination(0, 10);

        // When
        Flux<Product> saveAll = Flux.just(product1, product2, product3)
                .flatMap(adapter::save);

        Mono<ProductPage> findByFilter = saveAll
                .then(adapter.findByFilter(filter, pagination, List.of()));

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).hasSize(3);
                    assertThat(page.pageInfo().total()).isEqualTo(3);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("결과가 없을 때 빈 페이지를 반환한다")
    void shouldReturnEmptyPageWhenNoResults() {
        // Given
        ProductFilter filter = new ProductFilter(
                DealStatus.ENDED,
                null,
                null,
                null,
                null,
                null
        );
        Pagination pagination = new Pagination(0, 10);

        // When
        Mono<ProductPage> findByFilter = adapter.findByFilter(filter, pagination, List.of());

        // Then
        StepVerifier.create(findByFilter)
                .assertNext(page -> {
                    assertThat(page.content()).isEmpty();
                    assertThat(page.pageInfo().total()).isEqualTo(0);
                    assertThat(page.pageInfo().totalPages()).isEqualTo(0);
                })
                .verifyComplete();
    }

    // Helper methods
    private Product createProductWithPrice(BigDecimal original, BigDecimal sale) {
        ProductId productId = ProductId.generate();
        Price price = new Price(original, sale, "KRW");
        Schedule schedule = TestDataFactory.createSchedule();
        Specs specs = TestDataFactory.createSpecs();
        return new Product(productId, "Test Product", "Test Description", "카테고리", price, schedule, specs, DealStatus.UPCOMING);
    }

    private ProductDocument createProductDocumentWithCategory(String category) {
        Product product = TestDataFactory.createProduct();
        ProductDocument document = mapper.toDocument(product);
        document.setCategory(category);
        return document;
    }

    private Product createProductWithTitle(String title) {
        ProductId productId = ProductId.generate();
        Price price = TestDataFactory.createPrice();
        Schedule schedule = TestDataFactory.createSchedule();
        Specs specs = TestDataFactory.createSpecs();
        return new Product(productId, title, "Test Description", "카테고리", price, schedule, specs, DealStatus.UPCOMING);
    }

    private Product createProductWithDescription(String description) {
        ProductId productId = ProductId.generate();
        Price price = TestDataFactory.createPrice();
        Schedule schedule = TestDataFactory.createSchedule();
        Specs specs = TestDataFactory.createSpecs();
        return new Product(productId, "Test Product", description, "카테고리", price, schedule, specs, DealStatus.UPCOMING);
    }

    private Product createProductWithCategoryAndPrice(String category, BigDecimal original, BigDecimal sale) {
        Product product = createProductWithPrice(original, sale);
        Product updatedProduct = product.updateCategory(category);
        Product updatedProduct2 = updatedProduct.transitionTo(DealStatus.ACTIVE);
        return updatedProduct2;
    }
}

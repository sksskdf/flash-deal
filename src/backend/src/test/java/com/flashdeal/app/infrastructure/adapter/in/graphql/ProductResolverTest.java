package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.TestDataFactory;
import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductResolver 테스트")
class ProductResolverTest {

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private GetProductUseCase getProductUseCase;

    @Mock
    private UpdateProductUseCase updateProductUseCase;

    @InjectMocks
    private ProductResolver productResolver;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = TestDataFactory.createProduct();
    }

    @Test
    @DisplayName("product - 상품 조회 성공")
    void product_success() {
        // Given
        String productId = testProduct.getProductId().getValue();
        given(getProductUseCase.getProduct(any(ProductId.class)))
            .willReturn(Mono.just(testProduct));

        // When
        Mono<Product> result = productResolver.product(productId);

        // Then
        StepVerifier.create(result)
            .assertNext(product -> {
                assertThat(product.getProductId()).isEqualTo(testProduct.getProductId());
                assertThat(product.getTitle()).isEqualTo(testProduct.getTitle());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("products - 상태별 상품 조회")
    void products_byStatus() {
        // Given
        given(getProductUseCase.getProductsByStatus(DealStatus.ACTIVE))
            .willReturn(Flux.just(testProduct));

        // When
        Flux<Product> result = productResolver.products(DealStatus.ACTIVE, null);

        // Then
        StepVerifier.create(result)
            .assertNext(product -> assertThat(product.getStatus()).isEqualTo(DealStatus.ACTIVE))
            .verifyComplete();
    }

    @Test
    @DisplayName("products - 카테고리별 상품 조회")
    void products_byCategory() {
        // Given
        given(getProductUseCase.getProductsByCategory("Electronics"))
            .willReturn(Flux.just(testProduct));

        // When
        Flux<Product> result = productResolver.products(null, "Electronics");

        // Then
        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    @DisplayName("products - 파라미터 없을 때 활성 상품 조회")
    void products_withoutParams_returnsActiveProducts() {
        // Given
        given(getProductUseCase.getActiveProducts())
            .willReturn(Flux.just(testProduct));

        // When
        Flux<Product> result = productResolver.products(null, null);

        // Then
        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    @DisplayName("activeProducts - 활성 상품 목록 조회")
    void activeProducts_success() {
        // Given
        given(getProductUseCase.getActiveProducts())
            .willReturn(Flux.just(testProduct));

        // When
        Flux<Product> result = productResolver.activeProducts();

        // Then
        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete();
    }

    @Test
    @DisplayName("createProduct - 상품 생성 성공")
    void createProduct_success() {
        // Given
        ProductResolver.CreateProductInput input = new ProductResolver.CreateProductInput(
            "Test Product",
            "Test Description",
            new BigDecimal("100000"),
            new BigDecimal("80000"),
            "KRW",
            ZonedDateTime.now().plusHours(1),
            ZonedDateTime.now().plusDays(1),
            "Electronics",
            "https://example.com/image.jpg",
            null
        );

        given(createProductUseCase.createProduct(any(CreateProductUseCase.CreateProductCommand.class)))
            .willReturn(Mono.just(testProduct));

        // When
        Mono<Product> result = productResolver.createProduct(input);

        // Then
        StepVerifier.create(result)
            .assertNext(product -> assertThat(product).isNotNull())
            .verifyComplete();
    }

    @Test
    @DisplayName("updateProduct - 상품 수정 성공")
    void updateProduct_success() {
        // Given
        String productId = testProduct.getProductId().getValue();
        ProductResolver.UpdateProductInput input = new ProductResolver.UpdateProductInput(
            "Updated Title",
            "Updated Description",
            new BigDecimal("120000"),
            new BigDecimal("90000"),
            ZonedDateTime.now().plusHours(1),
            ZonedDateTime.now().plusDays(2)
        );

        Product updatedProduct = TestDataFactory.createProduct();
        updatedProduct.updateTitle("Updated Title");

        given(updateProductUseCase.updateProduct(any(UpdateProductUseCase.UpdateProductCommand.class)))
            .willReturn(Mono.just(updatedProduct));

        // When
        Mono<Product> result = productResolver.updateProduct(productId, input);

        // Then
        StepVerifier.create(result)
            .assertNext(product -> assertThat(product.getTitle()).isEqualTo("Updated Title"))
            .verifyComplete();
    }
}


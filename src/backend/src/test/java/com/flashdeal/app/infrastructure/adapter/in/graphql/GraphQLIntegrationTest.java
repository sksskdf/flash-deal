package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.TestDataFactory;
import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("GraphQL 통합 테스트")
class GraphQLIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateProductUseCase createProductUseCase;

    @MockBean
    private GetProductUseCase getProductUseCase;

    @MockBean
    private UpdateProductUseCase updateProductUseCase;

    @MockBean
    private CreateOrderUseCase createOrderUseCase;

    @MockBean
    private GetOrderUseCase getOrderUseCase;

    @MockBean
    private CancelOrderUseCase cancelOrderUseCase;

    @MockBean
    private CompletePaymentUseCase completePaymentUseCase;

    @MockBean
    private CreateInventoryUseCase createInventoryUseCase;

    @MockBean
    private GetInventoryUseCase getInventoryUseCase;

    @MockBean
    private ReserveInventoryUseCase reserveInventoryUseCase;

    @MockBean
    private ConfirmInventoryUseCase confirmInventoryUseCase;

    @MockBean
    private ReleaseInventoryUseCase releaseInventoryUseCase;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = TestDataFactory.createProduct();
    }

    @Test
    @DisplayName("product Query - 상품 조회")
    void productQuery() {
        // Given
        String productId = testProduct.getProductId().value();
        given(getProductUseCase.getProduct(any(ProductId.class)))
            .willReturn(Mono.just(testProduct));

        String query = """
            query {
              product(id: "%s") {
                productId
                title
                price {
                  sale
                }
              }
            }
            """.formatted(productId);

        // When & Then
        webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(java.util.Map.of("query", query))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.product.productId").isEqualTo(productId)
            .jsonPath("$.data.product.title").isEqualTo(testProduct.getTitle());
    }

    @Test
    @DisplayName("activeProducts Query - 활성 상품 목록 조회")
    void activeProductsQuery() {
        // Given
        given(getProductUseCase.getActiveProducts())
            .willReturn(Flux.just(testProduct));

        String query = """
            query {
              activeProducts {
                productId
                title
                price {
                  sale
                }
              }
            }
            """;

        // When & Then
        webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(java.util.Map.of("query", query))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.activeProducts[0].productId").exists()
            .jsonPath("$.data.activeProducts[0].title").isEqualTo(testProduct.getTitle());
    }

    @Test
    @DisplayName("createProduct Mutation - 상품 생성")
    void createProductMutation() {
        // Given
        given(createProductUseCase.createProduct(any(CreateProductUseCase.CreateProductCommand.class)))
            .willReturn(Mono.just(testProduct));

        String mutation = """
            mutation {
              createProduct(input: {
                title: "Test Product"
                originalPrice: "100000"
                dealPrice: "80000"
                currency: "KRW"
                startAt: "%s"
                endAt: "%s"
              }) {
                productId
                title
              }
            }
            """.formatted(
                ZonedDateTime.now().plusHours(1).toString(),
                ZonedDateTime.now().plusDays(1).toString()
            );

        // When & Then
        webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(java.util.Map.of("query", mutation))
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.data.createProduct.productId").exists()
            .jsonPath("$.data.createProduct.title").isEqualTo(testProduct.getTitle());
    }
}


package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.TestDataFactory;
import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.inventory.*;
import com.flashdeal.app.domain.product.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryResolver 테스트")
class InventoryResolverTest {

    @Mock
    private CreateInventoryUseCase createInventoryUseCase;

    @Mock
    private GetInventoryUseCase getInventoryUseCase;

    @Mock
    private ReserveInventoryUseCase reserveInventoryUseCase;

    @Mock
    private ConfirmInventoryUseCase confirmInventoryUseCase;

    @Mock
    private ReleaseInventoryUseCase releaseInventoryUseCase;

    @InjectMocks
    private InventoryResolver inventoryResolver;

    private Inventory testInventory;

    @BeforeEach
    void setUp() {
        testInventory = TestDataFactory.createInventory();
    }

    @Test
    @DisplayName("inventory - 재고 조회 성공")
    void inventory_success() {
        // Given
        String inventoryId = testInventory.inventoryId().value();
        given(getInventoryUseCase.getInventory(any(InventoryId.class)))
            .willReturn(Mono.just(testInventory));

        // When
        Mono<Inventory> result = inventoryResolver.inventory(inventoryId);

        // Then
        StepVerifier.create(result)
            .assertNext(inventory -> {
                assertThat(inventory.inventoryId()).isEqualTo(testInventory.inventoryId());
                    assertThat(inventory.stock().total()).isEqualTo(testInventory.stock().total());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("inventoryByProduct - 상품별 재고 조회 성공")
    void inventoryByProduct_success() {
        // Given
        String productId = testInventory.productId().value();
        given(getInventoryUseCase.getInventoryByProductId(any(ProductId.class)))
            .willReturn(Mono.just(testInventory));

        // When
        Mono<Inventory> result = inventoryResolver.inventoryByProduct(productId);

        // Then
        StepVerifier.create(result)
            .assertNext(inventory -> {
                assertThat(inventory.productId()).isEqualTo(testInventory.productId());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("createInventory - 재고 생성 성공")
    void createInventory_success() {
        // Given
        InventoryResolver.CreateInventoryInput input = new InventoryResolver.CreateInventoryInput(
            "product-123",
            1000,
            50,
            10,
            600
        );

        given(createInventoryUseCase.createInventory(any(CreateInventoryUseCase.CreateInventoryCommand.class)))
            .willReturn(Mono.just(testInventory));

        // When
        Mono<Inventory> result = inventoryResolver.createInventory(input);

        // Then
        StepVerifier.create(result)
            .assertNext(inventory -> assertThat(inventory).isNotNull())
            .verifyComplete();
    }

    @Test
    @DisplayName("reserveInventory - 재고 예약 성공")
    void reserveInventory_success() {
        // Given
        InventoryResolver.ReserveInventoryInput input = new InventoryResolver.ReserveInventoryInput(
            "product-123",
            2
        );

        given(reserveInventoryUseCase.reserve(any(ReserveInventoryUseCase.ReserveInventoryCommand.class)))
            .willReturn(Mono.empty());

        // When
        Mono<Boolean> result = inventoryResolver.reserveInventory(input);

        // Then
        StepVerifier.create(result)
            .assertNext(success -> assertThat(success).isTrue())
            .verifyComplete();
    }

    @Test
    @DisplayName("reserveInventory - 재고 예약 실패")
    void reserveInventory_failure() {
        // Given
        InventoryResolver.ReserveInventoryInput input = new InventoryResolver.ReserveInventoryInput(
            "product-123",
            2
        );

        given(reserveInventoryUseCase.reserve(any(ReserveInventoryUseCase.ReserveInventoryCommand.class)))
            .willReturn(Mono.error(new IllegalStateException("Out of stock")));

        // When
        Mono<Boolean> result = inventoryResolver.reserveInventory(input);

        // Then
        StepVerifier.create(result)
            .assertNext(success -> assertThat(success).isFalse())
            .verifyComplete();
    }

    @Test
    @DisplayName("confirmInventory - 재고 확정 성공")
    void confirmInventory_success() {
        // Given
        InventoryResolver.ReserveInventoryInput input = new InventoryResolver.ReserveInventoryInput(
            "product-123",
            2
        );

        given(confirmInventoryUseCase.confirm(any(ConfirmInventoryUseCase.ConfirmInventoryCommand.class)))
            .willReturn(Mono.empty());

        // When
        Mono<Boolean> result = inventoryResolver.confirmInventory(input);

        // Then
        StepVerifier.create(result)
            .assertNext(success -> assertThat(success).isTrue())
            .verifyComplete();
    }

    @Test
    @DisplayName("releaseInventory - 재고 해제 성공")
    void releaseInventory_success() {
        // Given
        InventoryResolver.ReserveInventoryInput input = new InventoryResolver.ReserveInventoryInput(
            "product-123",
            2
        );

        given(releaseInventoryUseCase.release(any(ReleaseInventoryUseCase.ReleaseInventoryCommand.class)))
            .willReturn(Mono.empty());

        // When
        Mono<Boolean> result = inventoryResolver.releaseInventory(input);

        // Then
        StepVerifier.create(result)
            .assertNext(success -> assertThat(success).isTrue())
            .verifyComplete();
    }
}


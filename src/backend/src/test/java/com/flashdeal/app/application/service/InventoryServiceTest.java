package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.ConfirmInventoryUseCase.ConfirmInventoryCommand;
import com.flashdeal.app.application.port.in.CreateInventoryUseCase.CreateInventoryCommand;
import com.flashdeal.app.application.port.in.ReleaseInventoryUseCase.ReleaseInventoryCommand;
import com.flashdeal.app.application.port.in.ReserveInventoryUseCase.ReserveInventoryCommand;
import com.flashdeal.app.application.port.out.InventoryRepository;
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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryService 테스트")
class InventoryServiceTest {

    @Mock
    InventoryRepository inventoryRepository;

    @InjectMocks
    InventoryService inventoryService;

    ProductId productId;
    Inventory baseInventory;

    @BeforeEach
    void setUp() {
        productId = new ProductId("P-1");
        baseInventory = new Inventory(
            new InventoryId("I-1"),
            productId,
            Stock.initial(100),
            new Policy(5, 600, 10)
        );
    }

    @Test
    @DisplayName("재고를 생성할 수 있다")
    void createInventory_success() {
        CreateInventoryCommand cmd = new CreateInventoryCommand(productId, 100, 5, 600, 10);
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(inventoryService.createInventory(cmd))
            .assertNext(inv -> {
                org.assertj.core.api.Assertions.assertThat(inv.getProductId()).isEqualTo(productId);
                org.assertj.core.api.Assertions.assertThat(inv.getStock().total()).isEqualTo(100);
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("재고를 조회할 수 있다")
    void getInventory_found() {
        given(inventoryRepository.findById(baseInventory.getInventoryId())).willReturn(Mono.just(baseInventory));

        StepVerifier.create(inventoryService.getInventory(baseInventory.getInventoryId()))
            .expectNext(baseInventory)
            .verifyComplete();
    }

    @Test
    @DisplayName("상품 ID로 재고를 조회할 수 있다")
    void getInventoryByProductId_found() {
        given(inventoryRepository.findByProductId(productId)).willReturn(Mono.just(baseInventory));

        StepVerifier.create(inventoryService.getInventoryByProductId(productId))
            .expectNext(baseInventory)
            .verifyComplete();
    }

    @Test
    @DisplayName("유효한 수량으로 재고를 예약할 수 있다")
    void reserve_valid_updatesInventory() {
        given(inventoryRepository.findByProductId(productId)).willReturn(Mono.just(baseInventory));
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(inventoryService.reserve(new ReserveInventoryCommand(productId, 3)))
            .verifyComplete();

        verify(inventoryRepository).save(any());
    }

    @Test
    @DisplayName("예약된 재고를 판매로 확정할 수 있다")
    void confirm_movesFromReservedToSold() {
        Inventory withReserved = new Inventory(
            baseInventory.getInventoryId(), productId,
            new Stock(100, 5, 95, 0), baseInventory.getPolicy()
        );
        given(inventoryRepository.findByProductId(productId)).willReturn(Mono.just(withReserved));
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(inventoryService.confirm(new ConfirmInventoryCommand(productId, 3)))
            .verifyComplete();
        verify(inventoryRepository).save(any());
    }

    @Test
    @DisplayName("예약된 재고를 사용 가능 상태로 해제할 수 있다")
    void release_returnsToAvailable() {
        Inventory withReserved = new Inventory(
            baseInventory.getInventoryId(), productId,
            new Stock(100, 5, 95, 0), baseInventory.getPolicy()
        );
        given(inventoryRepository.findByProductId(productId)).willReturn(Mono.just(withReserved));
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(inventoryService.release(new ReleaseInventoryCommand(productId, 2)))
            .verifyComplete();
        verify(inventoryRepository).save(any());
    }
}



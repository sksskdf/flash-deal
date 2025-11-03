package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.ConfirmInventoryUseCase.ConfirmInventoryCommand;
import com.flashdeal.app.application.port.in.CreateInventoryUseCase.CreateInventoryCommand;
import com.flashdeal.app.application.port.in.ReleaseInventoryUseCase.ReleaseInventoryCommand;
import com.flashdeal.app.application.port.in.ReserveInventoryUseCase.ReserveInventoryCommand;
import com.flashdeal.app.application.port.out.InventoryRepository;
import com.flashdeal.app.domain.inventory.*;
import com.flashdeal.app.domain.product.ProductId;
import org.junit.jupiter.api.BeforeEach;
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
    void createInventory_success() {
        CreateInventoryCommand cmd = new CreateInventoryCommand(productId, 100, 5, 600, 10);
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(inventoryService.createInventory(cmd))
            .assertNext(inv -> {
                org.assertj.core.api.Assertions.assertThat(inv.getProductId()).isEqualTo(productId);
                org.assertj.core.api.Assertions.assertThat(inv.getStock().getTotal()).isEqualTo(100);
            })
            .verifyComplete();
    }

    @Test
    void getInventory_found() {
        given(inventoryRepository.findById(baseInventory.getInventoryId())).willReturn(Mono.just(baseInventory));

        StepVerifier.create(inventoryService.getInventory(baseInventory.getInventoryId()))
            .expectNext(baseInventory)
            .verifyComplete();
    }

    @Test
    void getInventoryByProductId_found() {
        given(inventoryRepository.findByProductId(productId)).willReturn(Mono.just(baseInventory));

        StepVerifier.create(inventoryService.getInventoryByProductId(productId))
            .expectNext(baseInventory)
            .verifyComplete();
    }

    @Test
    void reserve_valid_updatesInventory() {
        given(inventoryRepository.findByProductId(productId)).willReturn(Mono.just(baseInventory));
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(inventoryService.reserve(new ReserveInventoryCommand(productId, 3)))
            .verifyComplete();

        verify(inventoryRepository).save(any());
    }

    @Test
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



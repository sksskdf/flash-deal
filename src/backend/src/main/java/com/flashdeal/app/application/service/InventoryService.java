package com.flashdeal.app.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.flashdeal.app.application.port.in.ConfirmInventoryUseCase;
import com.flashdeal.app.application.port.in.CreateInventoryUseCase;
import com.flashdeal.app.application.port.in.GetInventoryUseCase;
import com.flashdeal.app.application.port.in.ReleaseInventoryUseCase;
import com.flashdeal.app.application.port.in.ReserveInventoryUseCase;
import com.flashdeal.app.application.port.out.InventoryRepository;
import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.inventory.Policy;
import com.flashdeal.app.domain.inventory.Quantity;
import com.flashdeal.app.domain.inventory.Stock;
import com.flashdeal.app.domain.product.ProductId;

import reactor.core.publisher.Mono;

/**
 * Inventory Application Service
 */
@Service
public class InventoryService implements
        CreateInventoryUseCase,
        GetInventoryUseCase,
        ReserveInventoryUseCase,
        ConfirmInventoryUseCase,
        ReleaseInventoryUseCase {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Mono<Inventory> createInventory(CreateInventoryCommand command) {
        InventoryId inventoryId = new InventoryId(UUID.randomUUID().toString());

        Stock stock = new Stock(
                new Quantity(command.totalQuantity()),
                new Quantity(0),
                new Quantity(command.totalQuantity()),
                new Quantity(0));

        Policy policy = new Policy(
                command.lowStockThreshold(),
                command.reservationTimeout(),
                command.maxPurchaseQuantity());

        Inventory inventory = new Inventory(
                inventoryId,
                command.productId(),
                stock,
                policy);

        return inventoryRepository.save(inventory);
    }

    @Override
    public Mono<Inventory> getInventory(InventoryId inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Inventory not found: " + inventoryId)));
    }

    @Override
    public Mono<Inventory> getInventoryByProductId(ProductId productId) {
        return inventoryRepository.findByProductId(productId)
                .switchIfEmpty(
                        Mono.error(new IllegalArgumentException("Inventory not found for product: " + productId)));
    }

    @Override
    public Mono<Void> reserve(ReserveInventoryCommand command) {
        return inventoryRepository.findByProductId(command.productId())
                .switchIfEmpty(Mono
                        .error(new IllegalArgumentException("Inventory not found for product: " + command.productId())))
                .flatMap(inventory -> {
                    validateInventoryForReservation(inventory, command.quantity());
                    inventory.reserve(new Quantity(command.quantity()));
                    return inventoryRepository.save(inventory);
                })
                .then();
    }

    private void validateInventoryForReservation(Inventory inventory, int quantity) {
        if (!inventory.policy().isValidPurchaseQuantity(new Quantity(quantity))) {
            throw new IllegalArgumentException(
                    "Invalid purchase quantity: " + quantity +
                            " (max: " + inventory.policy().maxPurchasePerUser() + ")");
        }
        if (inventory.stock().outOfStock()) {
            throw new IllegalStateException("Product is out of stock");
        }
    }

    @Override
    public Mono<Void> confirm(ConfirmInventoryCommand command) {
        return inventoryRepository.findByProductId(command.productId())
                .switchIfEmpty(Mono
                        .error(new IllegalArgumentException("Inventory not found for product: " + command.productId())))
                .flatMap(inventory -> {
                    inventory.confirm(new Quantity(command.quantity()));
                    return inventoryRepository.save(inventory);
                })
                .then();
    }

    @Override
    public Mono<Void> release(ReleaseInventoryCommand command) {
        return inventoryRepository.findByProductId(command.productId())
                .switchIfEmpty(Mono
                        .error(new IllegalArgumentException("Inventory not found for product: " + command.productId())))
                .flatMap(inventory -> {
                    inventory.release(new Quantity(command.quantity()));
                    return inventoryRepository.save(inventory);
                })
                .then();
    }
}

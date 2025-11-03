package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.application.port.out.InventoryRepository;
import com.flashdeal.app.domain.inventory.*;
import com.flashdeal.app.domain.product.ProductId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
            command.totalQuantity(),
            0,
            command.totalQuantity(),
            0
        );
        
        Policy policy = new Policy(
            command.lowStockThreshold(),
            command.reservationTimeout(),
            command.maxPurchaseQuantity()
        );
        
        Inventory inventory = new Inventory(
            inventoryId,
            command.productId(),
            stock,
            policy
        );
        
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
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Inventory not found for product: " + productId)));
    }
    
    @Override
    public Mono<Void> reserve(ReserveInventoryCommand command) {
        return inventoryRepository.findByProductId(command.productId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Inventory not found for product: " + command.productId())))
            .flatMap(inventory -> {
                if (!inventory.isValidPurchaseQuantity(command.quantity())) {
                    return Mono.error(new IllegalArgumentException(
                        "Invalid purchase quantity: " + command.quantity() + 
                        " (max: " + inventory.getPolicy().getMaxPurchasePerUser() + ")"
                    ));
                }
                
                if (inventory.isOutOfStock()) {
                    return Mono.error(new IllegalStateException("Product is out of stock"));
                }
                
                inventory.reserve(command.quantity());
                return inventoryRepository.save(inventory);
            })
            .then();
    }
    
    @Override
    public Mono<Void> confirm(ConfirmInventoryCommand command) {
        return inventoryRepository.findByProductId(command.productId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Inventory not found for product: " + command.productId())))
            .flatMap(inventory -> {
                inventory.confirm(command.quantity());
                return inventoryRepository.save(inventory);
            })
            .then();
    }
    
    @Override
    public Mono<Void> release(ReleaseInventoryCommand command) {
        return inventoryRepository.findByProductId(command.productId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Inventory not found for product: " + command.productId())))
            .flatMap(inventory -> {
                inventory.release(command.quantity());
                return inventoryRepository.save(inventory);
            })
            .then();
    }
}

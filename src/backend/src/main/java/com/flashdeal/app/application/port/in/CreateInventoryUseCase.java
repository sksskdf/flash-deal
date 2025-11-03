package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

public interface CreateInventoryUseCase {
    
    Mono<Inventory> createInventory(CreateInventoryCommand command);
    
    record CreateInventoryCommand(
        ProductId productId,
        int totalQuantity,
        int lowStockThreshold,
        int maxPurchaseQuantity,
        int reservationTimeout
    ) {
        public CreateInventoryCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID is required");
            }
            if (totalQuantity <= 0) {
                throw new IllegalArgumentException("Total quantity must be positive");
            }
            if (lowStockThreshold < 0) {
                throw new IllegalArgumentException("Low stock threshold cannot be negative");
            }
            if (maxPurchaseQuantity <= 0) {
                throw new IllegalArgumentException("Max purchase quantity must be positive");
            }
            if (reservationTimeout <= 0) {
                throw new IllegalArgumentException("Reservation timeout must be positive");
            }
        }
    }
}

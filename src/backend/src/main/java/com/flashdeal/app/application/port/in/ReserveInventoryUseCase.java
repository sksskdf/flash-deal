package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

public interface ReserveInventoryUseCase {
    
    Mono<Void> reserve(ReserveInventoryCommand command);
    
    record ReserveInventoryCommand(
        ProductId productId,
        int quantity
    ) {
        public ReserveInventoryCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID is required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }
    }
}

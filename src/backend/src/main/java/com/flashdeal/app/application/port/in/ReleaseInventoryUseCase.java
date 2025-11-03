package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

public interface ReleaseInventoryUseCase {
    
    Mono<Void> release(ReleaseInventoryCommand command);
    
    record ReleaseInventoryCommand(
        ProductId productId,
        int quantity
    ) {
        public ReleaseInventoryCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID is required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }
    }
}

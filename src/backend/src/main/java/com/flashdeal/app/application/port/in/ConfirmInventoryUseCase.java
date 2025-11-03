package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

public interface ConfirmInventoryUseCase {
    
    Mono<Void> confirm(ConfirmInventoryCommand command);
    
    record ConfirmInventoryCommand(
        ProductId productId,
        int quantity
    ) {
        public ConfirmInventoryCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID is required");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }
    }
}

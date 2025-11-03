package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.product.Product;
import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface UpdateProductUseCase {
    
    Mono<Product> updateProduct(UpdateProductCommand command);
    
    record UpdateProductCommand(
        ProductId productId,
        String title,
        String description,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        ZonedDateTime startAt,
        ZonedDateTime endAt
    ) {
        public UpdateProductCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID is required");
            }
        }
    }
}

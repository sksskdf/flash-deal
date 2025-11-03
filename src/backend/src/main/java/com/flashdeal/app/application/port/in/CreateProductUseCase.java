package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.product.Product;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 상품 생성 Use Case
 */
public interface CreateProductUseCase {
    
    Mono<Product> createProduct(CreateProductCommand command);
    
    record CreateProductCommand(
        String title,
        String description,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        String currency,
        ZonedDateTime startAt,
        ZonedDateTime endAt,
        String category,
        String imageUrl
    ) {
        public CreateProductCommand {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Original price must be positive");
            }
            if (dealPrice == null || dealPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Deal price must be positive");
            }
            if (dealPrice.compareTo(originalPrice) >= 0) {
                throw new IllegalArgumentException("Deal price must be less than original price");
            }
            if (startAt == null || endAt == null) {
                throw new IllegalArgumentException("Start and end time are required");
            }
            if (!startAt.isBefore(endAt)) {
                throw new IllegalArgumentException("Start time must be before end time");
            }
        }
    }
}

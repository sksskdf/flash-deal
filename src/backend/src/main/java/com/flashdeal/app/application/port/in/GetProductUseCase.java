package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.product.Product;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.domain.product.DealStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 상품 조회 Use Case
 */
public interface GetProductUseCase {
    
    Mono<Product> getProduct(ProductId productId);
    
    Flux<Product> getProductsByStatus(DealStatus status);
    
    Flux<Product> getProductsByCategory(String category);
    
    Flux<Product> getActiveProducts();
}

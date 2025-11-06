package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.product.Product;
import com.flashdeal.app.domain.product.ProductFilter;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.domain.product.ProductPage;
import com.flashdeal.app.domain.product.SortOption;

import java.util.List;

import com.flashdeal.app.domain.product.DealStatus;
import com.flashdeal.app.domain.product.Pagination;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 상품 조회 Use Case
 */
public interface GetProductUseCase {
    
    Mono<Product> getProduct(ProductId productId);
    
    Flux<Product> getProductsByStatus(DealStatus status);
    
    Flux<Product> getProductsByCategory(String category);

    Mono<ProductPage> getProductsByFilter(ProductFilter filter, Pagination pagination, List<SortOption> sortOptions);
    
    Flux<Product> getActiveProducts();
}

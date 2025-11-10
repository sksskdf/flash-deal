package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.CreateProductUseCase;
import com.flashdeal.app.application.port.in.GetProductUseCase;
import com.flashdeal.app.application.port.in.UpdateProductUseCase;
import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.common.Pagination;
import com.flashdeal.app.domain.product.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Product Application Service
 */
@Service
public class ProductService implements CreateProductUseCase, GetProductUseCase, UpdateProductUseCase {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Mono<Product> createProduct(CreateProductCommand command) {
        ProductId productId = new ProductId(UUID.randomUUID().toString());
        Price price = createPrice(command.originalPrice(), command.dealPrice(), command.currency());
        Schedule schedule = createSchedule(command.startAt(), command.endAt());
        Specs specs = createSpecs(command.category(), command.imageUrl());
        
        Product product = new Product(
            productId,
            command.title(),
            command.description(),
            command.category(),
            price,
            schedule,
            specs
        );
        
        return productRepository.save(product);
    }

    private Price createPrice(java.math.BigDecimal originalPrice, java.math.BigDecimal dealPrice, String currency) {
        return new Price(originalPrice, dealPrice, currency != null ? currency : "KRW");
    }

    private Schedule createSchedule(java.time.ZonedDateTime startAt, java.time.ZonedDateTime endAt) {
        return new Schedule(startAt, endAt, "Asia/Seoul");
    }

    private Specs createSpecs(String category, String imageUrl) {
        Map<String, Object> specsMap = new java.util.HashMap<>();
        if (imageUrl != null) {
            specsMap.put("imageUrl", imageUrl);
        }
        return new Specs(specsMap);
    }
    
    @Override
    public Mono<Product> getProduct(ProductId productId) {
        return productRepository.findById(productId)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found: " + productId)));
    }
    
    @Override
    public Flux<Product> getProductsByStatus(DealStatus status) {
        return productRepository.findByStatus(status);
    }
    
    @Override
    public Flux<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    @Override
    public Flux<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    @Override
    public Mono<ProductPage> getProductsByFilter(ProductFilter filter, Pagination pagination,
            List<ProductSortOption> sortOptions) {
        return productRepository.findByFilter(filter, pagination, sortOptions);
    }
    
    @Override
    public Mono<Product> updateProduct(UpdateProductCommand command) {
        return productRepository.findById(command.productId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found: " + command.productId())))
            .flatMap(product -> {
                if (command.title() != null) {
                    product.updateTitle(command.title());
                }
                
                if (command.description() != null) {
                    product.updateDescription(command.description());
                }
                
                if (command.originalPrice() != null && command.dealPrice() != null) {
                    String currency = product.getPrice().currency();
                    Price newPrice = new Price(
                        command.originalPrice(),
                        command.dealPrice(),
                        currency
                    );
                    product.updatePrice(newPrice);
                }
                
                if (command.startAt() != null && command.endAt() != null) {
                    Schedule newSchedule = new Schedule(
                        command.startAt(),
                        command.endAt(),
                        "Asia/Seoul"
                    );
                    product.updateSchedule(newSchedule);
                }
                
                return productRepository.save(product);
            });
    }
}

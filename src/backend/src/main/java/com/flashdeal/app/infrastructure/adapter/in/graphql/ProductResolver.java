package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.product.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Product GraphQL Resolver
 */
@Controller
public class ProductResolver {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    public ProductResolver(
            CreateProductUseCase createProductUseCase,
            GetProductUseCase getProductUseCase,
            UpdateProductUseCase updateProductUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }

    @QueryMapping
    public Mono<Product> product(@Argument String id) {
        ProductId productId = new ProductId(id);
        return getProductUseCase.getProduct(productId);
    }

    @QueryMapping
    public Flux<Product> products(
            @Argument DealStatus status,
            @Argument String category) {
        
        if (status != null) {
            return getProductUseCase.getProductsByStatus(status);
        }
        
        if (category != null) {
            return getProductUseCase.getProductsByCategory(category);
        }
        
        return getProductUseCase.getActiveProducts();
    }

    @QueryMapping
    public Flux<Product> activeProducts() {
        return getProductUseCase.getActiveProducts();
    }

    @MutationMapping
    public Mono<Product> createProduct(@Argument CreateProductInput input) {
        CreateProductUseCase.CreateProductCommand command = 
            new CreateProductUseCase.CreateProductCommand(
                input.title(),
                input.description(),
                input.originalPrice(),
                input.dealPrice(),
                input.currency(),
                input.startAt(),
                input.endAt(),
                input.category(),
                input.imageUrl()
            );
        
        return createProductUseCase.createProduct(command);
    }

    @MutationMapping
    public Mono<Product> updateProduct(
            @Argument String id,
            @Argument UpdateProductInput input) {
        
        ProductId productId = new ProductId(id);
        
        UpdateProductUseCase.UpdateProductCommand command = 
            new UpdateProductUseCase.UpdateProductCommand(
                productId,
                input.title(),
                input.description(),
                input.originalPrice(),
                input.dealPrice(),
                input.startAt(),
                input.endAt()
            );
        
        return updateProductUseCase.updateProduct(command);
    }

    // Input/Output DTOs
    public record CreateProductInput(
        String title,
        String description,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        String currency,
        ZonedDateTime startAt,
        ZonedDateTime endAt,
        String category,
        String imageUrl,
        List<SpecFieldInput> specs
    ) {}

    public record UpdateProductInput(
        String title,
        String description,
        BigDecimal originalPrice,
        BigDecimal dealPrice,
        ZonedDateTime startAt,
        ZonedDateTime endAt
    ) {}

    public record SpecFieldInput(
        String key,
        String value
    ) {}
}


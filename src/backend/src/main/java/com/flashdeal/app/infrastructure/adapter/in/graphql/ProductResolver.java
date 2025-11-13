package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.common.Pagination;
import com.flashdeal.app.domain.common.SortOrder;
import com.flashdeal.app.domain.product.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
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
    public Mono<ProductPage> productsFiltered(
            @Argument ProductFilterInput filter,
            @Argument PaginationInput pagination,
            @Argument List<SortOptionInput> sort) {
        com.flashdeal.app.domain.product.ProductFilter domainFilter = mapToProductFilter(filter);
        Pagination domainPagination = mapToPagination(pagination);
        List<ProductSortOption> domainSortOptions = mapToSortOptions(sort);

        return getProductUseCase.getProductsByFilter(domainFilter, domainPagination, domainSortOptions);
    }

    private com.flashdeal.app.domain.product.ProductFilter mapToProductFilter(ProductFilterInput filter) {
        if (filter == null) {
            return new com.flashdeal.app.domain.product.ProductFilter(null, null, null, null, null, null);
        }
        return new com.flashdeal.app.domain.product.ProductFilter(
                filter.status(),
                filter.category(),
                filter.minPrice(),
                filter.maxPrice(),
                filter.minDiscountRate(),
                filter.searchText()
        );
    }

    private Pagination mapToPagination(PaginationInput pagination) {
        if (pagination == null) {
            return new Pagination(0, 20);
        }
        return new Pagination(pagination.page(), pagination.size());
    }

    private List<ProductSortOption> mapToSortOptions(List<SortOptionInput> sort) {
        if (sort == null) {
            return List.of(new ProductSortOption(ProductSortField.CREATED_AT, SortOrder.DESC));
        }
        return sort.stream()
                .map(s -> new ProductSortOption(
                        mapSortField(s.field().name()),
                        mapSortOrder(s.order().name())))
                .toList();
    }

    @QueryMapping
    public Flux<Product> activeProducts() {
        return getProductUseCase.getActiveProducts();
    }

    @SchemaMapping(typeName = "Product", field = "productId")
    public String productId(Product product) {
        return product.productId().value();
    }

    @SchemaMapping(typeName = "Product", field = "price")
    public Price price(Product product) {
        return product.price();
    }

    @SchemaMapping(typeName = "Product", field = "schedule")
    public Schedule schedule(Product product) {
        return product.schedule();
    }

    @SchemaMapping(typeName = "Product", field = "specs")
    public List<SpecField> specs(Product product) {
        return product.specs().getFields().entrySet().stream()
                .map(entry -> new SpecField(entry.getKey(), entry.getValue().toString()))
                .toList();
    }

    @SchemaMapping(typeName = "Price", field = "original")
    public BigDecimal original(Price price) {
        return price.original();
    }

    @SchemaMapping(typeName = "Price", field = "sale")
    public BigDecimal sale(Price price) {
        return price.sale();
    }

    @SchemaMapping(typeName = "Price", field = "currency")
    public String currency(Price price) {
        return price.currency();
    }

    @SchemaMapping(typeName = "Price", field = "discountRate")
    public int discountRate(Price price) {
        return price.discountRate();
    }

    @SchemaMapping(typeName = "Schedule", field = "startsAt")
    public ZonedDateTime startsAt(Schedule schedule) {
        return schedule.startsAt();
    }

    @SchemaMapping(typeName = "Schedule", field = "endsAt")
    public ZonedDateTime endsAt(Schedule schedule) {
        return schedule.endsAt();
    }

    @SchemaMapping(typeName = "Schedule", field = "timezone")
    public String timezone(Schedule schedule) {
        return schedule.timezone();
    }

    @SchemaMapping(typeName = "ProductPage", field = "content")
    public List<Product> content(ProductPage productPage) {
        return productPage.content();
    }

    @SchemaMapping(typeName = "ProductPage", field = "pageInfo")
    public com.flashdeal.app.domain.common.PageInfo pageInfo(ProductPage productPage) {
        return productPage.pageInfo();
    }

    @SchemaMapping(typeName = "PageInfo", field = "totalElements")
    public long totalElements(com.flashdeal.app.domain.common.PageInfo pageInfo) {
        return pageInfo.total();
    }

    @MutationMapping
    public Mono<Product> createProduct(@Argument CreateProductInput input) {
        CreateProductUseCase.CreateProductCommand command = new CreateProductUseCase.CreateProductCommand(
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
        UpdateProductUseCase.UpdateProductCommand command = new UpdateProductUseCase.UpdateProductCommand(
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

    public record SpecField(
        String key,
        String value
    ) {}

    public record ProductFilterInput(
            DealStatus status,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minDiscountRate,
            String searchText) {
    }

    public record SortOptionInput(
            ProductSortField field,
            SortOrder order) {
    }

    public record PaginationInput(
            Integer page,
            Integer size) {
    }

    private ProductSortField mapSortField(String field) {
        return ProductSortField.valueOf(field);
    }

    private SortOrder mapSortOrder(String order) {
        return SortOrder.valueOf(order);
    }
}


package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.product.*;

import java.math.BigDecimal;
import java.util.List;

import org.bson.types.Decimal128;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Product Persistence Adapter
 * 
 * ProductRepository Port의 MongoDB 구현체
 */
@Component
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductMongoRepository mongoRepository;
    private final ProductMapper mapper;
    private final ReactiveMongoTemplate mongoTemplate;

    public ProductPersistenceAdapter(ProductMongoRepository mongoRepository, ProductMapper mapper,
            ReactiveMongoTemplate mongoTemplate) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<Product> save(Product product) {
        ProductDocument document = mapper.toDocument(product);
        return mongoRepository.save(document)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Product> findById(ProductId id) {
        return mongoRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findByStatus(DealStatus status) {
        return mongoRepository.findByStatus(status)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findByCategory(String category) {
        return mongoRepository.findByCategory(category)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findActiveProducts() {
        return mongoRepository.findActiveProducts()
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findByStatusAndScheduleStartAtBefore(DealStatus status, java.time.Instant time) {
        return mongoRepository.findByStatusAndScheduleStartAtBefore(status, time)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Product> findByStatusAndScheduleEndAtBefore(DealStatus status, java.time.Instant time) {
        return mongoRepository.findByStatusAndScheduleEndAtBefore(status, time)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<ProductPage> findByFilter(ProductFilter filter, Pagination pagination, List<SortOption> sortOptions) {
        Query query = buildQuery(filter);
        Sort sort = buildSort(sortOptions);
        Pageable pageable = PageRequest.of(pagination.page(), pagination.size(), sort);

        Query paginatedQuery = query.with(pageable);
        Query countQuery = Query.of(query).limit(-1).skip(-1);

        Mono<Long> countMono = mongoTemplate.count(countQuery, ProductDocument.class);
        Flux<ProductDocument> documentsFlux = mongoTemplate.find(paginatedQuery, ProductDocument.class);

        return Mono.zip(documentsFlux.collectList(), countMono)
                .map(tuple -> createProductPage(tuple.getT1(), tuple.getT2(), pagination));
    }

    private ProductPage createProductPage(List<ProductDocument> documents, long total, Pagination pagination) {
        List<Product> products = documents.stream()
                .map(mapper::toDomain)
                .toList();

        int totalPages = (int) Math.ceil((double) total / pagination.size());
        boolean hasNext = pagination.page() < (total / pagination.size());
        boolean hasPrevious = pagination.page() > 0;

        PageInfo pageInfo = new PageInfo(
                pagination.page(),
                pagination.size(),
                total,
                totalPages,
                hasNext,
                hasPrevious);

        return new ProductPage(products, pageInfo);
    }

    @Override
    public Mono<Void> deleteById(ProductId id) {
        return mongoRepository.deleteById(id.getValue());
    }

    @Override
    public Mono<Boolean> existsById(ProductId id) {
        return mongoRepository.existsById(id.getValue());
    }

    private Query buildQuery(ProductFilter filter) {
        Query query = new Query();

        addStatusCriteria(query, filter.status());
        addCategoryCriteria(query, filter.category());
        addPriceCriteria(query, filter.minPrice(), filter.maxPrice());
        addDiscountRateCriteria(query, filter.minDiscountRate());
        addSearchTextCriteria(query, filter.searchText());

        return query;
    }

    private void addStatusCriteria(Query query, DealStatus status) {
        if (status != null) {
            query.addCriteria(Criteria.where("status").is(status));
        }
    }

    private void addCategoryCriteria(Query query, String category) {
        if (category != null) {
            query.addCriteria(Criteria.where("category").is(category));
        }
    }

    private void addPriceCriteria(Query query, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return;
        }

        Criteria priceCriteria = Criteria.where("price.sale");
        if (minPrice != null && maxPrice != null) {
            priceCriteria = priceCriteria.gte(toDecimal128(minPrice)).lte(toDecimal128(maxPrice));
        } else if (minPrice != null) {
            priceCriteria = priceCriteria.gte(toDecimal128(minPrice));
        } else {
            priceCriteria = priceCriteria.lte(toDecimal128(maxPrice));
        }
        query.addCriteria(priceCriteria);
    }

    private Decimal128 toDecimal128(BigDecimal value) {
        return new Decimal128(value);
    }

    private void addDiscountRateCriteria(Query query, Integer minDiscountRate) {
        if (minDiscountRate != null) {
            query.addCriteria(Criteria.where("price.rate").gte(minDiscountRate));
        }
    }

    private void addSearchTextCriteria(Query query, String searchText) {
        if (searchText != null && !searchText.isBlank()) {
            query.addCriteria(new Criteria().orOperator(
                    new Criteria("title").regex(searchText, "i"),
                    new Criteria("description").regex(searchText, "i"),
                    new Criteria("category").regex(searchText, "i")));
        }
    }

    private Sort buildSort(List<SortOption> sortOptions) {
        if (sortOptions == null || sortOptions.isEmpty()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }

        List<Sort.Order> orders = sortOptions.stream()
                .map(option -> {
                    String field = mapSortField(option.field());
                    Sort.Direction direction = option.order() == SortOrder.ASC
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC;
                    return new Sort.Order(direction, field);
                }).toList();

        return Sort.by(orders);
    }

    private String mapSortField(ProductSortField field) {
        return switch (field) {
            case TITLE -> "title";
            case PRICE -> "price.sale";
            case DISCOUNT_RATE -> "price.rate";
            case CREATED_AT -> "createdAt";
            case STARTS_AT -> "schedule.startsAt";
            case ENDS_AT -> "schedule.endsAt";
        };
    }
}

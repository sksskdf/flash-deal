package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.product.*;
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

    public ProductPersistenceAdapter(ProductMongoRepository mongoRepository, ProductMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
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
    public Mono<Void> deleteById(ProductId id) {
        return mongoRepository.deleteById(id.getValue());
    }

    @Override
    public Mono<Boolean> existsById(ProductId id) {
        return mongoRepository.existsById(id.getValue());
    }
}






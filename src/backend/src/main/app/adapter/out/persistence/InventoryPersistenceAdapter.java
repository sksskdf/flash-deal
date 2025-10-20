package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.application.port.out.InventoryRepository;
import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.product.ProductId;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Inventory Persistence Adapter
 * 
 * InventoryRepository Port의 MongoDB 구현체
 */
@Component
public class InventoryPersistenceAdapter implements InventoryRepository {

    private final InventoryMongoRepository mongoRepository;
    private final InventoryMapper mapper;

    public InventoryPersistenceAdapter(InventoryMongoRepository mongoRepository, InventoryMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Inventory> save(Inventory inventory) {
        InventoryDocument document = mapper.toDocument(inventory);
        return mongoRepository.save(document)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Inventory> findById(InventoryId id) {
        return mongoRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Inventory> findByProductId(ProductId productId) {
        return mongoRepository.findByProductId(productId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(InventoryId id) {
        return mongoRepository.deleteById(id.getValue());
    }

    @Override
    public Mono<Boolean> existsByProductId(ProductId productId) {
        return mongoRepository.existsByProductId(productId.getValue());
    }
}






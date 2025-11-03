package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.application.port.out.OrderRepository;
import com.flashdeal.app.domain.order.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Order Persistence Adapter
 * 
 * OrderRepository Port의 MongoDB 구현체
 */
@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderMongoRepository mongoRepository;
    private final OrderMapper mapper;

    public OrderPersistenceAdapter(OrderMongoRepository mongoRepository, OrderMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Order> save(Order order) {
        OrderDocument document = mapper.toDocument(order);
        return mongoRepository.save(document)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Order> findById(OrderId id) {
        return mongoRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Order> findByUserId(UserId userId) {
        return mongoRepository.findByUserId(userId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Order> findByStatus(OrderStatus status) {
        return mongoRepository.findByStatus(status)
                .map(mapper::toDomain);
    }

    @Override
    public Flux<Order> findByUserIdAndStatus(UserId userId, OrderStatus status) {
        return mongoRepository.findByUserIdAndStatus(userId.getValue(), status)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Order> findByIdempotencyKey(String idempotencyKey) {
        return mongoRepository.findAllByIdempotencyKey(idempotencyKey)
                .map(mapper::toDomain)
                .next();
    }

    @Override
    public Flux<Order> findByStatusAndCreatedAtBefore(OrderStatus status, java.time.Instant time) {
        return mongoRepository.findByStatusAndCreatedAtBefore(status, time)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(OrderId id) {
        return mongoRepository.deleteById(id.getValue());
    }

    @Override
    public Mono<Boolean> existsById(OrderId id) {
        return mongoRepository.existsById(id.getValue());
    }
}






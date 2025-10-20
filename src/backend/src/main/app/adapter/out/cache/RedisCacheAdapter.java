package com.flashdeal.app.adapter.out.cache;

import com.flashdeal.app.domain.product.ProductId;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Redis Cache Adapter
 * 
 * 재고 캐싱을 위한 Redis Adapter
 * .doc/data/2.redis-strategy.md의 전략을 기반으로 구현
 */
@Component
public class RedisCacheAdapter {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private static final String INVENTORY_KEY_PREFIX = "inventory:";
    private static final String RESERVATION_KEY_PREFIX = "reservation:";
    private static final Duration RESERVATION_TTL = Duration.ofMinutes(10);

    public RedisCacheAdapter(ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 재고 조회
     */
    public Mono<Integer> getInventory(ProductId productId) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.opsForValue()
                .get(key)
                .cast(Integer.class)
                .defaultIfEmpty(0);
    }

    /**
     * 재고 설정
     */
    public Mono<Boolean> setInventory(ProductId productId, int quantity) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.opsForValue()
                .set(key, quantity);
    }

    /**
     * 재고 감소 (원자적 연산)
     */
    public Mono<Long> decrementInventory(ProductId productId, int quantity) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.opsForValue()
                .decrement(key, quantity);
    }

    /**
     * 재고 증가 (원자적 연산)
     */
    public Mono<Long> incrementInventory(ProductId productId, int quantity) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.opsForValue()
                .increment(key, quantity);
    }

    /**
     * 재고 예약 설정
     */
    public Mono<Boolean> setReservation(ProductId productId, String orderId, int quantity) {
        String key = RESERVATION_KEY_PREFIX + productId.getValue();
        return redisTemplate.opsForZSet()
                .add(key, orderId, System.currentTimeMillis())
                .then(redisTemplate.expire(key, RESERVATION_TTL))
                .then(Mono.just(true));
    }

    /**
     * 재고 예약 해제
     */
    public Mono<Long> removeReservation(ProductId productId, String orderId) {
        String key = RESERVATION_KEY_PREFIX + productId.getValue();
        return redisTemplate.opsForZSet()
                .remove(key, orderId);
    }

    /**
     * 재고 예약 조회
     */
    public Mono<Long> getReservationCount(ProductId productId) {
        String key = RESERVATION_KEY_PREFIX + productId.getValue();
        return redisTemplate.opsForZSet()
                .count(key, org.springframework.data.domain.Range.closed(0.0, (double) System.currentTimeMillis()));
    }

    /**
     * 재고 키 삭제
     */
    public Mono<Boolean> deleteInventory(ProductId productId) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.delete(key)
                .map(count -> count > 0);
    }

    /**
     * 재고 키 존재 여부 확인
     */
    public Mono<Boolean> existsInventory(ProductId productId) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.hasKey(key);
    }

    /**
     * 재고 TTL 설정
     */
    public Mono<Boolean> setInventoryTTL(ProductId productId, Duration ttl) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.expire(key, ttl);
    }

    /**
     * 재고 TTL 조회
     */
    public Mono<Duration> getInventoryTTL(ProductId productId) {
        String key = INVENTORY_KEY_PREFIX + productId.getValue();
        return redisTemplate.getExpire(key);
    }
}

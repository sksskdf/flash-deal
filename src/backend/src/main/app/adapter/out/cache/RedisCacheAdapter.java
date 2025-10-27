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

    // ===== 분산 락 구현 =====
    
    private static final String LOCK_KEY_PREFIX = "lock:";
    private static final Duration DEFAULT_LOCK_TTL = Duration.ofSeconds(10);
    
    /**
     * 분산 락 획득
     * 
     * @param resourceId 락을 걸 리소스 ID (예: productId)
     * @param lockId 락 소유자 ID (UUID)
     * @param ttl 락 만료 시간
     * @return 락 획득 성공 여부
     */
    public Mono<Boolean> acquireLock(String resourceId, String lockId, Duration ttl) {
        String lockKey = LOCK_KEY_PREFIX + resourceId;
        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockId, ttl)
                .map(result -> result != null && result);
    }
    
    /**
     * 분산 락 획득 (기본 TTL 사용)
     */
    public Mono<Boolean> acquireLock(String resourceId, String lockId) {
        return acquireLock(resourceId, lockId, DEFAULT_LOCK_TTL);
    }
    
    /**
     * 분산 락 해제
     * 
     * @param resourceId 락을 걸 리소스 ID
     * @param lockId 락 소유자 ID
     * @return 락 해제 성공 여부
     */
    public Mono<Boolean> releaseLock(String resourceId, String lockId) {
        String lockKey = LOCK_KEY_PREFIX + resourceId;
        
        // Lua 스크립트로 원자적 락 해제
        String luaScript = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "  return redis.call('del', KEYS[1]) " +
            "else " +
            "  return 0 " +
            "end";
        
        org.springframework.data.redis.core.script.RedisScript<Long> script = 
            org.springframework.data.redis.core.script.RedisScript.of(luaScript, Long.class);
        
               return redisTemplate.execute(script, java.util.Collections.singletonList(lockKey), java.util.Collections.singletonList(lockId))
                       .next()
                       .map(result -> result > 0);
    }
    
    /**
     * 분산 락 연장
     * 
     * @param resourceId 락을 걸 리소스 ID
     * @param lockId 락 소유자 ID
     * @param ttl 새로운 TTL
     * @return 락 연장 성공 여부
     */
    public Mono<Boolean> extendLock(String resourceId, String lockId, Duration ttl) {
        String lockKey = LOCK_KEY_PREFIX + resourceId;
        
        // Lua 스크립트로 원자적 락 연장
        String luaScript = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "  return redis.call('expire', KEYS[1], ARGV[2]) " +
            "else " +
            "  return 0 " +
            "end";
        
        org.springframework.data.redis.core.script.RedisScript<Long> script = 
            org.springframework.data.redis.core.script.RedisScript.of(luaScript, Long.class);
        
               return redisTemplate.execute(script, java.util.Collections.singletonList(lockKey), java.util.Arrays.asList(lockId, String.valueOf(ttl.getSeconds())))
                       .next()
                       .map(result -> result > 0);
    }
    
    /**
     * 락 소유자 확인
     * 
     * @param resourceId 락을 걸 리소스 ID
     * @return 락 소유자 ID (없으면 null)
     */
    public Mono<String> getLockOwner(String resourceId) {
        String lockKey = LOCK_KEY_PREFIX + resourceId;
        return redisTemplate.opsForValue()
                .get(lockKey)
                .cast(String.class);
    }
    
    /**
     * 락 존재 여부 확인
     * 
     * @param resourceId 락을 걸 리소스 ID
     * @return 락 존재 여부
     */
    public Mono<Boolean> isLocked(String resourceId) {
        String lockKey = LOCK_KEY_PREFIX + resourceId;
        return redisTemplate.hasKey(lockKey);
    }
    
    /**
     * 락 TTL 조회
     * 
     * @param resourceId 락을 걸 리소스 ID
     * @return 락 TTL
     */
    public Mono<Duration> getLockTTL(String resourceId) {
        String lockKey = LOCK_KEY_PREFIX + resourceId;
        return redisTemplate.getExpire(lockKey);
    }
    
    // ===== 재고 원자적 연산 (분산 락 사용) =====
    
    /**
     * 분산 락을 사용한 안전한 재고 감소
     * 
     * @param productId 상품 ID
     * @param quantity 감소할 수량
     * @param lockId 락 ID
     * @return 감소 후 재고 수량
     */
    public Mono<Long> safeDecrementInventory(ProductId productId, int quantity, String lockId) {
        String resourceId = productId.getValue();
        String inventoryKey = INVENTORY_KEY_PREFIX + resourceId;
        
        // Lua 스크립트로 원자적 재고 감소 (락 확인 포함)
        String luaScript = 
            "local lockKey = 'lock:' .. ARGV[3] " +
            "local inventoryKey = ARGV[4] " +
            "local lockOwner = redis.call('get', lockKey) " +
            "if lockOwner ~= ARGV[1] then " +
            "  return -1 " +  // 락 소유자가 아님
            "end " +
            "local current = redis.call('get', inventoryKey) " +
            "if not current then " +
            "  return -2 " +  // 재고 정보 없음
            "end " +
            "current = tonumber(current) " +
            "if current < tonumber(ARGV[2]) then " +
            "  return -3 " +  // 재고 부족
            "end " +
            "return redis.call('decrby', inventoryKey, ARGV[2])";
        
        org.springframework.data.redis.core.script.RedisScript<Long> script = 
            org.springframework.data.redis.core.script.RedisScript.of(luaScript, Long.class);
        
        return redisTemplate.execute(script, java.util.Collections.emptyList(), java.util.Arrays.asList(lockId, String.valueOf(quantity), resourceId, inventoryKey))
                .next()
                .map(result -> {
                    if (result == -1) {
                        throw new RuntimeException("Lock ownership mismatch");
                    } else if (result == -2) {
                        throw new RuntimeException("Inventory not found");
                    } else if (result == -3) {
                        throw new RuntimeException("Insufficient stock");
                    }
                    return result;
                });
    }
    
    /**
     * 분산 락을 사용한 안전한 재고 증가
     */
    public Mono<Long> safeIncrementInventory(ProductId productId, int quantity, String lockId) {
        String resourceId = productId.getValue();
        String inventoryKey = INVENTORY_KEY_PREFIX + resourceId;
        
        String luaScript = 
            "local lockKey = 'lock:' .. ARGV[3] " +
            "local inventoryKey = ARGV[4] " +
            "local lockOwner = redis.call('get', lockKey) " +
            "if lockOwner ~= ARGV[1] then " +
            "  return -1 " +
            "end " +
            "return redis.call('incrby', inventoryKey, ARGV[2])";
        
        org.springframework.data.redis.core.script.RedisScript<Long> script = 
            org.springframework.data.redis.core.script.RedisScript.of(luaScript, Long.class);
        
        return redisTemplate.execute(script, java.util.Collections.emptyList(), java.util.Arrays.asList(lockId, String.valueOf(quantity), resourceId, inventoryKey))
                .next()
                .map(result -> {
                    if (result == -1) {
                        throw new RuntimeException("Lock ownership mismatch");
                    }
                    return result;
                });
    }
}

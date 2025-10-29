package com.flashdeal.app;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

import java.time.Duration;

/**
 * Redis 전용 통합 테스트 베이스 클래스
 * Redis 슬라이스 테스트에서만 사용
 */
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractRedisIntegrationTest {

    @Container
    public static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7-alpine"))
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(30))
            .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", () -> redisContainer.getFirstMappedPort());
    }
}

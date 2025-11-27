package com.flashdeal.app;

import java.time.Duration;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

/**
 * 전체 통합 테스트용 베이스 클래스
 * MongoDB, Redis, Kafka가 모두 필요한 통합 테스트에서만 사용
 */
@Testcontainers
@ActiveProfiles("test")
@SuppressWarnings("resource")
public abstract class AbstractFullIntegrationTest {

        @Container
        public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(
                        DockerImageName.parse("mongo:4.4.2"))
                        .withReuse(true)
                        .withStartupTimeout(Duration.ofSeconds(60))
                        .waitingFor(Wait.forLogMessage(".*Waiting for connections.*", 1)
                                        .withStartupTimeout(Duration.ofSeconds(60)));

        @Container
        public static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:7-alpine"))
                        .withReuse(true)
                        .withStartupTimeout(Duration.ofSeconds(30))
                        .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));

        @Container
        public static final KafkaContainer kafkaContainer = new KafkaContainer(
                        DockerImageName.parse("confluentinc/cp-kafka:6.2.2"))
                        .withReuse(true)
                        .withStartupTimeout(Duration.ofSeconds(60))
                        .waitingFor(Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60)));

        @DynamicPropertySource
        static void setProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
                registry.add("spring.data.redis.host", redisContainer::getHost);
                registry.add("spring.data.redis.port", () -> redisContainer.getFirstMappedPort());
                registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        }
}

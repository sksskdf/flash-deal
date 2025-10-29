package com.flashdeal.app;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * MongoDB 전용 통합 테스트 베이스 클래스
 * MongoDB 슬라이스 테스트에서만 사용
 */
@Testcontainers
@ActiveProfiles("test")
public abstract class AbstractMongoIntegrationTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"))
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(60))
            .waitingFor(Wait.forLogMessage(".*Waiting for connections.*", 1)
                    .withStartupTimeout(Duration.ofSeconds(60)));

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
}

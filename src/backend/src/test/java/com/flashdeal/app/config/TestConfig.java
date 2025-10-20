package com.flashdeal.app.config;

import it.ozimov.cirneco.hamcrest.java7.AssertJava7;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import redis.embedded.RedisServer;

import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * 테스트용 Embedded 서버 설정
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    private RedisServer redisServer;
    private EmbeddedKafkaBroker kafkaBroker;

    @Bean
    @Primary
    public RedisServer embeddedRedisServer() throws IOException {
        redisServer = new RedisServer(6379);
        redisServer.start();
        return redisServer;
    }

    @Bean
    @Primary
    public RedisConnectionFactory testRedisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    @Primary
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        kafkaBroker = new EmbeddedKafkaBroker(1, true, "test-topic");
        kafkaBroker.afterPropertiesSet();
        return kafkaBroker;
    }

    @PreDestroy
    public void cleanup() {
        if (redisServer != null) {
            redisServer.stop();
        }
        if (kafkaBroker != null) {
            kafkaBroker.destroy();
        }
    }
}



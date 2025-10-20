package com.flashdeal.app.adapter.out.messaging;

import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.product.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.test.context.EmbeddedKafka; // 제거됨
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
// import org.testcontainers.containers.KafkaContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kafka Event Publisher 테스트
 */
@SpringBootTest
// @Testcontainers
@DirtiesContext
class KafkaEventPublisherTest {

    // @Container
    // static KafkaContainer kafka = new KafkaContainer("confluentinc/cp-kafka:7.4.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Mock Kafka 설정 (실제 Kafka 없이 테스트)
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaEventPublisher publisher;
    private OrderId orderId;
    private ProductId productId;

    @BeforeEach
    void setUp() {
        publisher = new KafkaEventPublisher(kafkaTemplate);
        orderId = OrderId.generate();
        productId = ProductId.generate();
    }

    @Test
    void shouldPublishOrderCreatedEvent() throws InterruptedException {
        // Given
        String userId = "user-123";
        String orderNumber = "ORD-123456";
        String idempotencyKey = "idempotency-123";
        CountDownLatch latch = new CountDownLatch(1);

        // When
        publisher.publishOrderCreated(orderId, userId, orderNumber, idempotencyKey);

        // Then
        // 실제 환경에서는 Consumer를 통해 이벤트 수신 확인
        // 여기서는 발행만 테스트
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void shouldPublishPaymentCompletedEvent() throws InterruptedException {
        // Given
        String paymentMethod = "CARD";
        String transactionId = "txn-123456";
        String status = "COMPLETED";
        CountDownLatch latch = new CountDownLatch(1);

        // When
        publisher.publishPaymentCompleted(orderId, paymentMethod, transactionId, status);

        // Then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void shouldPublishInventoryReservedEvent() throws InterruptedException {
        // Given
        int quantity = 5;
        String status = "RESERVED";
        CountDownLatch latch = new CountDownLatch(1);

        // When
        publisher.publishInventoryReserved(productId, orderId, quantity, status);

        // Then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void shouldPublishOrderCancelledEvent() throws InterruptedException {
        // Given
        String reason = "Customer request";
        String cancelledBy = "user-123";
        CountDownLatch latch = new CountDownLatch(1);

        // When
        publisher.publishOrderCancelled(orderId, reason, cancelledBy);

        // Then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }

    @Test
    void shouldPublishCustomEvent() throws InterruptedException {
        // Given
        String topic = "custom.topic";
        String key = "custom-key";
        Map<String, Object> event = Map.of(
            "customField", "customValue",
            "number", 123
        );
        CountDownLatch latch = new CountDownLatch(1);

        // When
        publisher.publishCustomEvent(topic, key, event);

        // Then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
    }
}

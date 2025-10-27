package com.flashdeal.app.adapter.out.messaging;

import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.product.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

/**
 * Kafka Event Publisher 테스트
 * Mock을 사용하여 실제 Kafka 연결 없이 테스트
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaEventPublisher publisher;
    private OrderId orderId;
    private ProductId productId;

    @BeforeEach
    void setUp() {
        // Mock KafkaTemplate의 send 메서드가 성공적인 CompletableFuture를 반환하도록 설정
        CompletableFuture<SendResult<String, Object>> successfulFuture = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), any(Object.class))).thenReturn(successfulFuture);
        
        publisher = new KafkaEventPublisher(kafkaTemplate);
        orderId = OrderId.generate();
        productId = ProductId.generate();
    }

    @Test
    void shouldPublishOrderCreatedEvent() {
        // Given
        String userId = "user-123";
        String orderNumber = "ORD-123456";
        String idempotencyKey = "idempotency-123";

        // When & Then
        // 예외가 발생하지 않으면 성공
        assertThatCode(() -> {
            publisher.publishOrderCreated(orderId, userId, orderNumber, idempotencyKey);
        }).doesNotThrowAnyException();
    }

    @Test
    void shouldPublishPaymentCompletedEvent() {
        // Given
        String paymentMethod = "CARD";
        String transactionId = "txn-123456";
        String status = "COMPLETED";

        // When & Then
        assertThatCode(() -> {
            publisher.publishPaymentCompleted(orderId, paymentMethod, transactionId, status);
        }).doesNotThrowAnyException();
    }

    @Test
    void shouldPublishInventoryReservedEvent() {
        // Given
        int quantity = 5;
        String status = "RESERVED";

        // When & Then
        assertThatCode(() -> {
            publisher.publishInventoryReserved(productId, orderId, quantity, status);
        }).doesNotThrowAnyException();
    }

    @Test
    void shouldPublishOrderCancelledEvent() {
        // Given
        String reason = "Customer request";
        String cancelledBy = "user-123";

        // When & Then
        assertThatCode(() -> {
            publisher.publishOrderCancelled(orderId, reason, cancelledBy);
        }).doesNotThrowAnyException();
    }

    @Test
    void shouldPublishCustomEvent() {
        // Given
        String topic = "custom.topic";
        String key = "custom-key";
        Map<String, Object> event = new HashMap<>();
        event.put("customField", "customValue");
        event.put("number", 123);

        // When & Then
        assertThatCode(() -> {
            publisher.publishCustomEvent(topic, key, event);
        }).doesNotThrowAnyException();
    }
}

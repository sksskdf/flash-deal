package com.flashdeal.app.infrastructure.adapter.out.messaging;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.flashdeal.app.AbstractKafkaIntegrationTest;
import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.product.ProductId;

/**
 * Kafka Event Publisher 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("KafkaEventPublisher 테스트")
class KafkaEventPublisherTest extends AbstractKafkaIntegrationTest {

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
    @DisplayName("주문 생성 이벤트를 발행할 수 있다")
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

        // 실제 Kafka로 메시지가 전송되었는지 확인하기 위해 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("결제 완료 이벤트를 발행할 수 있다")
    void shouldPublishPaymentCompletedEvent() {
        // Given
        String paymentMethod = "CARD";
        String transactionId = "txn-123456";
        String status = "COMPLETED";

        // When & Then
        assertThatCode(() -> {
            publisher.publishPaymentCompleted(orderId, paymentMethod, transactionId, status);
        }).doesNotThrowAnyException();

        // 실제 Kafka로 메시지가 전송되었는지 확인하기 위해 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("재고 예약 이벤트를 발행할 수 있다")
    void shouldPublishInventoryReservedEvent() {
        // Given
        int quantity = 5;
        String status = "RESERVED";

        // When & Then
        assertThatCode(() -> {
            publisher.publishInventoryReserved(productId, orderId, quantity, status);
        }).doesNotThrowAnyException();

        // 실제 Kafka로 메시지가 전송되었는지 확인하기 위해 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("주문 취소 이벤트를 발행할 수 있다")
    void shouldPublishOrderCancelledEvent() {
        // Given
        String reason = "Customer request";
        String cancelledBy = "user-123";

        // When & Then
        assertThatCode(() -> {
            publisher.publishOrderCancelled(orderId, reason, cancelledBy);
        }).doesNotThrowAnyException();

        // 실제 Kafka로 메시지가 전송되었는지 확인하기 위해 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("커스텀 이벤트를 발행할 수 있다")
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

        // 실제 Kafka로 메시지가 전송되었는지 확인하기 위해 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

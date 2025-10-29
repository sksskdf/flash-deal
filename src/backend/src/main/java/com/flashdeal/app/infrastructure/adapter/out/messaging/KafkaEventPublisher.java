package com.flashdeal.app.infrastructure.adapter.out.messaging;

import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.product.ProductId;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka Event Publisher
 * 
 * .doc/data/3.kafka-events.md의 이벤트 스키마를 기반으로 구현
 */
@Component
public class KafkaEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Kafka Topics
    private static final String ORDER_CREATED_TOPIC = "order.created";
    private static final String PAYMENT_COMPLETED_TOPIC = "payment.completed";
    private static final String INVENTORY_RESERVED_TOPIC = "inventory.reserved";
    private static final String ORDER_CANCELLED_TOPIC = "order.cancelled";

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 주문 생성 이벤트 발행
     */
    public void publishOrderCreated(OrderId orderId, String userId, String orderNumber, String idempotencyKey) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "OrderCreated");
        event.put("orderId", orderId.getValue());
        event.put("userId", userId);
        event.put("orderNumber", orderNumber);
        event.put("idempotencyKey", idempotencyKey);
        event.put("timestamp", ZonedDateTime.now());
        event.put("correlationId", UUID.randomUUID().toString());

        kafkaTemplate.send(ORDER_CREATED_TOPIC, orderId.getValue(), event);
    }

    /**
     * 결제 완료 이벤트 발행
     */
    public void publishPaymentCompleted(OrderId orderId, String paymentMethod, String transactionId, String status) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "PaymentCompleted");
        event.put("orderId", orderId.getValue());
        event.put("paymentMethod", paymentMethod);
        event.put("transactionId", transactionId);
        event.put("status", status);
        event.put("timestamp", ZonedDateTime.now());
        event.put("correlationId", UUID.randomUUID().toString());

        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, orderId.getValue(), event);
    }

    /**
     * 재고 예약 이벤트 발행
     */
    public void publishInventoryReserved(ProductId productId, OrderId orderId, int quantity, String status) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "InventoryReserved");
        event.put("productId", productId.getValue());
        event.put("orderId", orderId.getValue());
        event.put("quantity", quantity);
        event.put("status", status);
        event.put("timestamp", ZonedDateTime.now());
        event.put("correlationId", UUID.randomUUID().toString());

        kafkaTemplate.send(INVENTORY_RESERVED_TOPIC, productId.getValue(), event);
    }

    /**
     * 주문 취소 이벤트 발행
     */
    public void publishOrderCancelled(OrderId orderId, String reason, String cancelledBy) {
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", "OrderCancelled");
        event.put("orderId", orderId.getValue());
        event.put("reason", reason);
        event.put("cancelledBy", cancelledBy);
        event.put("timestamp", ZonedDateTime.now());
        event.put("correlationId", UUID.randomUUID().toString());

        kafkaTemplate.send(ORDER_CANCELLED_TOPIC, orderId.getValue(), event);
    }

    /**
     * 커스텀 이벤트 발행
     */
    public void publishCustomEvent(String topic, String key, Map<String, Object> event) {
        event.put("eventId", UUID.randomUUID().toString());
        event.put("timestamp", ZonedDateTime.now());
        event.put("correlationId", UUID.randomUUID().toString());

        kafkaTemplate.send(topic, key, event);
    }
}






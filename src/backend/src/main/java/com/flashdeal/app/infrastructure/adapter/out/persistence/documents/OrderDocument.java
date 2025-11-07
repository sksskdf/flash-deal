package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import com.flashdeal.app.domain.order.OrderStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Order MongoDB Document
 * 
 * .doc/data/1.model-structure.md의 Order 스키마를 기반으로 구현
 */
@Document(collection = "orders")
public class OrderDocument {

    @Id
    private String id;

    @Field("orderNumber")
    private String orderNumber;

    @Field("user")
    private UserInfoDocument user;

    @Field("items")
    private List<OrderItemDocument> items;

    @Field("pricing")
    private PricingDocument pricing;

    @Field("shipping")
    private ShippingDocument shipping;

    @Field("payment")
    private PaymentDocument payment;

    @Field("status")
    private OrderStatus status;

    @Field("statusHistory")
    private List<StatusChangeDocument> statusHistory;

    @Field("kafkaEvents")
    private List<KafkaEventDocument> kafkaEvents;

    @Field("idempotencyKey")
    private String idempotencyKey;

    @Field("cancellation")
    private CancellationDocument cancellation;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("createdAt")
    private Instant createdAt;

    @Field("updatedAt")
    private Instant updatedAt;

    // Constructors
    public OrderDocument() {}

    public OrderDocument(String id, String orderNumber, UserInfoDocument user, List<OrderItemDocument> items,
                        PricingDocument pricing, ShippingDocument shipping, PaymentDocument payment,
                        OrderStatus status, List<StatusChangeDocument> statusHistory,
                        List<KafkaEventDocument> kafkaEvents, String idempotencyKey,
                        CancellationDocument cancellation, Map<String, Object> metadata,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.user = user;
        this.items = items;
        this.pricing = pricing;
        this.shipping = shipping;
        this.payment = payment;
        this.status = status;
        this.statusHistory = statusHistory;
        this.kafkaEvents = kafkaEvents;
        this.idempotencyKey = idempotencyKey;
        this.cancellation = cancellation;
        this.metadata = metadata;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public UserInfoDocument getUser() {
        return user;
    }

    public void setUser(UserInfoDocument user) {
        this.user = user;
    }

    public List<OrderItemDocument> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDocument> items) {
        this.items = items;
    }

    public PricingDocument getPricing() {
        return pricing;
    }

    public void setPricing(PricingDocument pricing) {
        this.pricing = pricing;
    }

    public ShippingDocument getShipping() {
        return shipping;
    }

    public void setShipping(ShippingDocument shipping) {
        this.shipping = shipping;
    }

    public PaymentDocument getPayment() {
        return payment;
    }

    public void setPayment(PaymentDocument payment) {
        this.payment = payment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<StatusChangeDocument> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusChangeDocument> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public List<KafkaEventDocument> getKafkaEvents() {
        return kafkaEvents;
    }

    public void setKafkaEvents(List<KafkaEventDocument> kafkaEvents) {
        this.kafkaEvents = kafkaEvents;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public CancellationDocument getCancellation() {
        return cancellation;
    }

    public void setCancellation(CancellationDocument cancellation) {
        this.cancellation = cancellation;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}






package com.flashdeal.app.infrastructure.adapter.out.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Inventory MongoDB Document
 * 
 * .doc/data/1.model-structure.md의 Inventory 스키마를 기반으로 구현
 */
@Document(collection = "inventory")
public class InventoryDocument {

    @Id
    private String id;

    @Field("productId")
    private String productId;

    @Field("stock")
    private StockDocument stock;

    @Field("level")
    private String level;

    @Field("redis")
    private RedisInfoDocument redis;

    @Field("policy")
    private PolicyDocument policy;

    @Field("thresholds")
    private ThresholdsDocument thresholds;

    @Field("events")
    private List<EventDocument> events;

    @Field("adjustments")
    private List<AdjustmentDocument> adjustments;

    @Field("createdAt")
    private Instant createdAt;

    @Field("updatedAt")
    private Instant updatedAt;

    // Constructors
    public InventoryDocument() {}

    public InventoryDocument(String id, String productId, StockDocument stock, String level,
                           RedisInfoDocument redis, PolicyDocument policy, ThresholdsDocument thresholds,
                           List<EventDocument> events, List<AdjustmentDocument> adjustments,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.productId = productId;
        this.stock = stock;
        this.level = level;
        this.redis = redis;
        this.policy = policy;
        this.thresholds = thresholds;
        this.events = events;
        this.adjustments = adjustments;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public StockDocument getStock() {
        return stock;
    }

    public void setStock(StockDocument stock) {
        this.stock = stock;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public RedisInfoDocument getRedis() {
        return redis;
    }

    public void setRedis(RedisInfoDocument redis) {
        this.redis = redis;
    }

    public PolicyDocument getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyDocument policy) {
        this.policy = policy;
    }

    public ThresholdsDocument getThresholds() {
        return thresholds;
    }

    public void setThresholds(ThresholdsDocument thresholds) {
        this.thresholds = thresholds;
    }

    public List<EventDocument> getEvents() {
        return events;
    }

    public void setEvents(List<EventDocument> events) {
        this.events = events;
    }

    public List<AdjustmentDocument> getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(List<AdjustmentDocument> adjustments) {
        this.adjustments = adjustments;
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






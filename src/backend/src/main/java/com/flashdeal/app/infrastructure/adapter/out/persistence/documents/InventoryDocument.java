package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "inventories")
public class InventoryDocument {

    @Id
    private String id;
    private String productId;
    private StockDocument stock;
    private String level;
    private RedisInfoDocument redis;
    private PolicyDocument policy;
    private ThresholdsDocument thresholds;
    private List<Object> events; // Using Object for now, can be a specific document
    private List<Object> adjustments; // Using Object for now, can be a specific document
    private Instant createdAt;
    private Instant updatedAt;

    public InventoryDocument(String id, String productId, StockDocument stock, String level, RedisInfoDocument redis, PolicyDocument policy, ThresholdsDocument thresholds, List<Object> events, List<Object> adjustments, Instant createdAt, Instant updatedAt) {
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

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public StockDocument getStock() {
        return stock;
    }

    public String getLevel() {
        return level;
    }

    public RedisInfoDocument getRedis() {
        return redis;
    }

    public PolicyDocument getPolicy() {
        return policy;
    }

    public ThresholdsDocument getThresholds() {
        return thresholds;
    }

    public List<Object> getEvents() {
        return events;
    }

    public List<Object> getAdjustments() {
        return adjustments;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
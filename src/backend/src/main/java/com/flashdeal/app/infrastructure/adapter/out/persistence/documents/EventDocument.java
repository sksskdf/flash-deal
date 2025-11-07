package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;
import java.util.Map;

/**
 * Event MongoDB Document
 */
public class EventDocument {
    
    private String type;
    private int amount;
    private int before;
    private int after;
    private String reason;
    private String orderId;
    private Instant timestamp;
    private Map<String, Object> metadata;

    public EventDocument() {}

    public EventDocument(String type, int amount, int before, int after, String reason, 
                        String orderId, Instant timestamp, Map<String, Object> metadata) {
        this.type = type;
        this.amount = amount;
        this.before = before;
        this.after = after;
        this.reason = reason;
        this.orderId = orderId;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBefore() {
        return before;
    }

    public void setBefore(int before) {
        this.before = before;
    }

    public int getAfter() {
        return after;
    }

    public void setAfter(int after) {
        this.after = after;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}






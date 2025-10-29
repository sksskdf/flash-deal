package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.order.OrderStatus;
import java.time.Instant;

/**
 * Status Change MongoDB Document
 */
public class StatusChangeDocument {
    
    private OrderStatus from;
    private OrderStatus to;
    private String reason;
    private Instant timestamp;
    private String changedBy;

    public StatusChangeDocument() {}

    public StatusChangeDocument(OrderStatus from, OrderStatus to, String reason,
                              Instant timestamp, String changedBy) {
        this.from = from;
        this.to = to;
        this.reason = reason;
        this.timestamp = timestamp;
        this.changedBy = changedBy;
    }

    public OrderStatus getFrom() {
        return from;
    }

    public void setFrom(OrderStatus from) {
        this.from = from;
    }

    public OrderStatus getTo() {
        return to;
    }

    public void setTo(OrderStatus to) {
        this.to = to;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}






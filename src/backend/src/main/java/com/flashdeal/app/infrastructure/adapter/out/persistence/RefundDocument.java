package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Refund MongoDB Document
 */
public class RefundDocument {
    
    private String id;
    private BigDecimal amount;
    private String reason;
    private String status;
    private Instant requestedAt;
    private Instant processedAt;

    public RefundDocument() {}

    public RefundDocument(String id, BigDecimal amount, String reason, String status,
                        Instant requestedAt, Instant processedAt) {
        this.id = id;
        this.amount = amount;
        this.reason = reason;
        this.status = status;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}






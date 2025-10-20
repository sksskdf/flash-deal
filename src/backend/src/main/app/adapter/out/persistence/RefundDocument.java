package com.flashdeal.app.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Refund MongoDB Document
 */
public class RefundDocument {
    
    private String id;
    private BigDecimal amount;
    private String reason;
    private String status;
    private ZonedDateTime requestedAt;
    private ZonedDateTime processedAt;

    public RefundDocument() {}

    public RefundDocument(String id, BigDecimal amount, String reason, String status,
                        ZonedDateTime requestedAt, ZonedDateTime processedAt) {
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

    public ZonedDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(ZonedDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public ZonedDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(ZonedDateTime processedAt) {
        this.processedAt = processedAt;
    }
}






package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.time.Instant;
import java.math.BigDecimal;

public class RefundDocument {
    private String id;
    private BigDecimal amount;
    private String reason;
    private String status;
    private Instant requestedAt;
    private Instant processedAt;

    public RefundDocument(String id, BigDecimal amount, String reason, String status, Instant requestedAt,
            Instant processedAt) {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
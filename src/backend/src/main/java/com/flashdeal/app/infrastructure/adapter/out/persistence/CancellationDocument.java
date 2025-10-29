package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.time.Instant;

/**
 * Cancellation MongoDB Document
 */
public class CancellationDocument {
    
    private String reason;
    private String requestedBy;
    private Instant requestedAt;
    private Instant processedAt;
    private String status;

    public CancellationDocument() {}

    public CancellationDocument(String reason, String requestedBy, Instant requestedAt,
                              Instant processedAt, String status) {
        this.reason = reason;
        this.requestedBy = requestedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}






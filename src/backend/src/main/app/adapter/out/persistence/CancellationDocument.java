package com.flashdeal.app.adapter.out.persistence;

import java.time.ZonedDateTime;

/**
 * Cancellation MongoDB Document
 */
public class CancellationDocument {
    
    private String reason;
    private String requestedBy;
    private ZonedDateTime requestedAt;
    private ZonedDateTime processedAt;
    private String status;

    public CancellationDocument() {}

    public CancellationDocument(String reason, String requestedBy, ZonedDateTime requestedAt,
                              ZonedDateTime processedAt, String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}






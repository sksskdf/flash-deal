package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;
import java.util.List;

public class CancellationDocument {
    private Boolean isCancelled;
    private String reason;
    private String cancelledBy;
    private Instant cancelledAt;
    private List<String> items;

    public CancellationDocument(Boolean isCancelled, String reason, String cancelledBy, Instant cancelledAt, List<String> items) {
        this.isCancelled = isCancelled;
        this.reason = reason;
        this.cancelledBy = cancelledBy;
        this.cancelledAt = cancelledAt;
        this.items = items;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public String getReason() {
        return reason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public List<String> getItems() {
        return items;
    }
}
package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.time.Instant;

/**
 * Adjustment MongoDB Document
 */
public class AdjustmentDocument {
    
    private String type;
    private int amount;
    private String reason;
    private String adjustedBy;
    private Instant timestamp;

    public AdjustmentDocument() {}

    public AdjustmentDocument(String type, int amount, String reason, String adjustedBy, Instant timestamp) {
        this.type = type;
        this.amount = amount;
        this.reason = reason;
        this.adjustedBy = adjustedBy;
        this.timestamp = timestamp;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAdjustedBy() {
        return adjustedBy;
    }

    public void setAdjustedBy(String adjustedBy) {
        this.adjustedBy = adjustedBy;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}






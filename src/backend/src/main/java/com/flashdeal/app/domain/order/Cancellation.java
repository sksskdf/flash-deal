package com.flashdeal.app.domain.order;

import java.time.Instant;
import java.util.List;

public class Cancellation {
    private final Boolean isCancelled;
    private final String reason;
    private final String cancelledBy;
    private final Instant cancelledAt;
    private final List<String> items;

    public Cancellation(Boolean isCancelled, String reason, String cancelledBy, Instant cancelledAt,
            List<String> items) {
        this.isCancelled = isCancelled;
        this.reason = reason;
        this.cancelledBy = cancelledBy;
        this.cancelledAt = cancelledAt;
        this.items = items;
    }

    public Cancellation(String reason, String cancelledBy, List<String> items) {
        this(true, reason, cancelledBy, Instant.now(), items);
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

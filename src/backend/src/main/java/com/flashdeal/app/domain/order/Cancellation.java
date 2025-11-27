package com.flashdeal.app.domain.order;

import java.time.Instant;
import java.util.List;

public record Cancellation(
    Boolean isCancelled,
    String reason,
    String cancelledBy,
    Instant cancelledAt,
    List<String> items
) {
    public Cancellation(String reason, String cancelledBy, List<String> items) {
        this(true, reason, cancelledBy, Instant.now(), items);
    }
}

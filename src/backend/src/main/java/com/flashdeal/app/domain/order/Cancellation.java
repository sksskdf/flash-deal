package com.flashdeal.app.domain.order;

import com.flashdeal.app.domain.product.ProductId;

import java.time.Instant;
import java.util.List;

public class Cancellation {
    private final String reason;
    private final List<ProductId> cancelledItems;
    private final Instant cancelledAt;

    public Cancellation(String reason, List<ProductId> cancelledItems) {
        this.reason = reason;
        this.cancelledItems = cancelledItems;
        this.cancelledAt = Instant.now();
    }

    public String getReason() {
        return reason;
    }

    public List<ProductId> getCancelledItems() {
        return cancelledItems;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }
}

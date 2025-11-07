package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;
import java.math.BigDecimal;

public class InstallmentDocument {
    private Integer count;
    private BigDecimal amount;
    private Instant firstDue;
    private Instant lastDue;

    public InstallmentDocument(Integer count, BigDecimal amount, Instant firstDue, Instant lastDue) {
        this.count = count;
        this.amount = amount;
        this.firstDue = firstDue;
        this.lastDue = lastDue;
    }

    public Integer getCount() {
        return count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getFirstDue() {
        return firstDue;
    }

    public Instant getLastDue() {
        return lastDue;
    }
}
package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Installment MongoDB Document
 */
public class InstallmentDocument {
    
    private int count;
    private BigDecimal amount;
    private Instant firstDue;
    private Instant lastDue;

    public InstallmentDocument() {}

    public InstallmentDocument(int count, BigDecimal amount, Instant firstDue, Instant lastDue) {
        this.count = count;
        this.amount = amount;
        this.firstDue = firstDue;
        this.lastDue = lastDue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getFirstDue() {
        return firstDue;
    }

    public void setFirstDue(Instant firstDue) {
        this.firstDue = firstDue;
    }

    public Instant getLastDue() {
        return lastDue;
    }

    public void setLastDue(Instant lastDue) {
        this.lastDue = lastDue;
    }
}






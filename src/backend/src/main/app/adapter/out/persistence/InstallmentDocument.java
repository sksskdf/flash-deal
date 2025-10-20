package com.flashdeal.app.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Installment MongoDB Document
 */
public class InstallmentDocument {
    
    private int count;
    private BigDecimal amount;
    private ZonedDateTime firstDue;
    private ZonedDateTime lastDue;

    public InstallmentDocument() {}

    public InstallmentDocument(int count, BigDecimal amount, ZonedDateTime firstDue, ZonedDateTime lastDue) {
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

    public ZonedDateTime getFirstDue() {
        return firstDue;
    }

    public void setFirstDue(ZonedDateTime firstDue) {
        this.firstDue = firstDue;
    }

    public ZonedDateTime getLastDue() {
        return lastDue;
    }

    public void setLastDue(ZonedDateTime lastDue) {
        this.lastDue = lastDue;
    }
}






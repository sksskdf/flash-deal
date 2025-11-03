package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.Map;

public class PricingDocument {
    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private String currency;
    private Map<String, Object> breakdown;

    public PricingDocument(BigDecimal subtotal, BigDecimal shipping, BigDecimal tax, BigDecimal discount, BigDecimal total, String currency, Map<String, Object> breakdown) {
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.tax = tax;
        this.discount = discount;
        this.total = total;
        this.currency = currency;
        this.breakdown = breakdown;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getCurrency() {
        return currency;
    }

    public Map<String, Object> getBreakdown() {
        return breakdown;
    }
}
package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Pricing MongoDB Document
 */
public class PricingDocument {
    
    private BigDecimal subtotal;
    private BigDecimal shipping;
    private BigDecimal tax;
    private BigDecimal discount;
    private BigDecimal total;
    private String currency;
    private Map<String, Object> breakdown;

    public PricingDocument() {}

    public PricingDocument(BigDecimal subtotal, BigDecimal shipping, BigDecimal tax,
                         BigDecimal discount, BigDecimal total, String currency,
                         Map<String, Object> breakdown) {
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

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Map<String, Object> getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(Map<String, Object> breakdown) {
        this.breakdown = breakdown;
    }
}






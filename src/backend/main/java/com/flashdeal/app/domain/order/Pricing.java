package com.flashdeal.app.domain.order;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 주문 금액 Value Object
 * 
 * 불변식:
 * - total = subtotal + shipping - discount
 * - subtotal, shipping, discount >= 0
 */
public final class Pricing {
    
    private final BigDecimal subtotal;
    private final BigDecimal shipping;
    private final BigDecimal discount;
    private final BigDecimal total;
    private final String currency;

    public Pricing(BigDecimal subtotal, BigDecimal shipping, BigDecimal discount, String currency) {
        validateNotNull(subtotal, "Subtotal");
        validateNotNull(shipping, "Shipping");
        validateNotNull(discount, "Discount");
        validateNotNull(currency, "Currency");
        
        if (currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be empty");
        }
        
        validateNonNegative(subtotal, "Subtotal");
        validateNonNegative(shipping, "Shipping");
        validateNonNegative(discount, "Discount");
        
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.discount = discount;
        this.total = subtotal.add(shipping).subtract(discount);
        this.currency = currency;
    }

    private void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private void validateNonNegative(BigDecimal value, String fieldName) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShipping() {
        return shipping;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pricing pricing = (Pricing) o;
        return Objects.equals(subtotal, pricing.subtotal) &&
               Objects.equals(shipping, pricing.shipping) &&
               Objects.equals(discount, pricing.discount) &&
               Objects.equals(currency, pricing.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subtotal, shipping, discount, currency);
    }

    @Override
    public String toString() {
        return "Pricing{" +
                "subtotal=" + subtotal +
                ", shipping=" + shipping +
                ", discount=" + discount +
                ", total=" + total +
                ", currency='" + currency + '\'' +
                '}';
    }
}


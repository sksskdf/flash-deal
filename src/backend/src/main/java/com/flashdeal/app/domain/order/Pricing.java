package com.flashdeal.app.domain.order;

import java.math.BigDecimal;

public record Pricing(
        BigDecimal subtotal,
        BigDecimal shipping,
        BigDecimal discount,
        String currency) {
    public Pricing {
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
    }

    private static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private static void validateNonNegative(BigDecimal value, String fieldName) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    public BigDecimal total() {
        return subtotal.add(shipping).subtract(discount);
    }
}


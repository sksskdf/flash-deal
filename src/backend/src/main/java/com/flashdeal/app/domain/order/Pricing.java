package com.flashdeal.app.domain.order;

import static com.flashdeal.app.domain.validator.Validator.validateNonNegative;
import static com.flashdeal.app.domain.validator.Validator.validateNotNull;

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

    public BigDecimal total() {
        return subtotal.add(shipping).subtract(discount);
    }
}

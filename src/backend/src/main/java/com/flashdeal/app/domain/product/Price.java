package com.flashdeal.app.domain.product;

import static com.flashdeal.app.domain.validator.Validator.validateNonNegative;
import static com.flashdeal.app.domain.validator.Validator.validateNotEmpty;
import static com.flashdeal.app.domain.validator.Validator.validateNotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Price(
    BigDecimal original,
    BigDecimal sale,
    String currency
) {
    public Price {
        validateNotNull(original, "Original price cannot be null");
        validateNotNull(sale, "Sale price cannot be null");
        validateNotEmpty(currency, "Currency cannot be null or empty");
        validateNonNegative(original, "Original price cannot be negative");
        validateNonNegative(sale, "Sale price cannot be negative");
        
        if (sale.compareTo(original) > 0) {
            throw new IllegalArgumentException("Sale price cannot be greater than original price");
        }
    }

    public int discountRate() {
        if (original.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        
        BigDecimal discount = original.subtract(sale);
        BigDecimal rate = discount.divide(original, 4, RoundingMode.HALF_UP)
                                  .multiply(new BigDecimal("100"));

        return rate.setScale(0, RoundingMode.HALF_UP).intValue();
    }
}


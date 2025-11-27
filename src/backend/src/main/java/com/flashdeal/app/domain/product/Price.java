package com.flashdeal.app.domain.product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Price(
    BigDecimal original,
    BigDecimal sale,
    String currency
) {
    public Price {
        if (original == null) {
            throw new IllegalArgumentException("Original price cannot be null");
        }
        if (sale == null) {
            throw new IllegalArgumentException("Sale price cannot be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency cannot be null");
        }
        
        if (currency.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency cannot be empty");
        }
        
        if (original.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Original price cannot be negative");
        }
        
        if (sale.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sale price cannot be negative");
        }
        
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


package com.flashdeal.app.domain.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 가격 Value Object
 */
public final class Price {
    
    private final BigDecimal original;
    private final BigDecimal sale;
    private final String currency;

    public Price(BigDecimal original, BigDecimal sale, String currency) {
        validateNotNull(original, "Original price cannot be null");
        validateNotNull(sale, "Sale price cannot be null");
        validateNotNull(currency, "Currency cannot be null");
        
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
        
        this.original = original;
        this.sale = sale;
        this.currency = currency;
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 할인율 계산 (original - sale) / original × 100
     */
    public int discountRate() {
        if (original.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        
        BigDecimal discount = original.subtract(sale);
        BigDecimal rate = discount.divide(original, 4, RoundingMode.HALF_UP)
                                  .multiply(new BigDecimal("100"));
        
        return rate.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    public BigDecimal getOriginal() {
        return original;
    }

    public BigDecimal getSale() {
        return sale;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(original, price.original) &&
               Objects.equals(sale, price.sale) &&
               Objects.equals(currency, price.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original, sale, currency);
    }

    @Override
    public String toString() {
        return "Price{" +
                "original=" + original +
                ", sale=" + sale +
                ", currency='" + currency + '\'' +
                '}';
    }
}


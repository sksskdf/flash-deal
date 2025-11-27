package com.flashdeal.app.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Price Value Object 테스트")
class PriceTest {

    @Test
    @DisplayName("유효한 가격으로 Price를 생성할 수 있다")
    void createPriceWithValidValues() {
        // given
        BigDecimal original = new BigDecimal("100000");
        BigDecimal sale = new BigDecimal("80000");
        String currency = "KRW";
        
        // when
        Price price = new Price(original, sale, currency);
        
        // then
        assertNotNull(price);
        assertEquals(original, price.original());
        assertEquals(sale, price.sale());
        assertEquals(currency, price.currency());
    }

    @Test
    @DisplayName("할인가가 정가보다 크면 예외가 발생한다")
    void throwsExceptionWhenSalePriceIsGreaterThanOriginal() {
        // given
        BigDecimal original = new BigDecimal("80000");
        BigDecimal sale = new BigDecimal("100000");
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Price(original, sale, "KRW"));
    }

    @Test
    @DisplayName("정가가 음수이면 예외가 발생한다")
    void throwsExceptionWhenOriginalPriceIsNegative() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Price(new BigDecimal("-100"), BigDecimal.ZERO, "KRW"));
    }

    @Test
    @DisplayName("할인가가 음수이면 예외가 발생한다")
    void throwsExceptionWhenSalePriceIsNegative() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Price(new BigDecimal("100"), new BigDecimal("-10"), "KRW"));
    }

    @Test
    @DisplayName("할인율을 계산할 수 있다")
    void calculateDiscountRate() {
        // given
        Price price = new Price(
            new BigDecimal("100000"), 
            new BigDecimal("80000"), 
            "KRW"
        );
        
        // when
        int discountRate = price.discountRate();
        
        // then
        assertEquals(20, discountRate);
    }

    @Test
    @DisplayName("할인이 없으면 할인율은 0이다")
    void discountRateIsZeroWhenNoDiscount() {
        // given
        Price price = new Price(
            new BigDecimal("100000"), 
            new BigDecimal("100000"), 
            "KRW"
        );
        
        // when
        int discountRate = price.discountRate();
        
        // then
        assertEquals(0, discountRate);
    }

    @Test
    @DisplayName("같은 가격은 동일하다")
    void testEquality() {
        // given
        Price price1 = new Price(
            new BigDecimal("100000"), 
            new BigDecimal("80000"), 
            "KRW"
        );
        Price price2 = new Price(
            new BigDecimal("100000"), 
            new BigDecimal("80000"), 
            "KRW"
        );
        
        // then
        assertEquals(price1, price2);
        assertEquals(price1.hashCode(), price2.hashCode());
    }

    @Test
    @DisplayName("통화가 null이면 예외가 발생한다")
    void throwsExceptionWhenCurrencyIsNull() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Price(new BigDecimal("100"), new BigDecimal("80"), null));
    }

    @Test
    @DisplayName("통화가 빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenCurrencyIsEmpty() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Price(new BigDecimal("100"), new BigDecimal("80"), ""));
    }
}


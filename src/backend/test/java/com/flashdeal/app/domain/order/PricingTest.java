package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pricing Value Object 테스트")
class PricingTest {

    @Test
    @DisplayName("유효한 값으로 Pricing을 생성할 수 있다")
    void createPricingWithValidValues() {
        // when
        Pricing pricing = new Pricing(
            new BigDecimal("100000"),
            new BigDecimal("3000"),
            new BigDecimal("5000"),
            "KRW"
        );
        
        // then
        assertNotNull(pricing);
        assertEquals(new BigDecimal("100000"), pricing.getSubtotal());
        assertEquals(new BigDecimal("3000"), pricing.getShipping());
        assertEquals(new BigDecimal("5000"), pricing.getDiscount());
        assertEquals(new BigDecimal("98000"), pricing.getTotal());
        assertEquals("KRW", pricing.getCurrency());
    }

    @Test
    @DisplayName("할인이 없으면 total = subtotal + shipping")
    void calculateTotalWithoutDiscount() {
        // when
        Pricing pricing = new Pricing(
            new BigDecimal("100000"),
            new BigDecimal("3000"),
            BigDecimal.ZERO,
            "KRW"
        );
        
        // then
        assertEquals(new BigDecimal("103000"), pricing.getTotal());
    }

    @Test
    @DisplayName("배송비가 없으면 total = subtotal - discount")
    void calculateTotalWithoutShipping() {
        // when
        Pricing pricing = new Pricing(
            new BigDecimal("100000"),
            BigDecimal.ZERO,
            new BigDecimal("5000"),
            "KRW"
        );
        
        // then
        assertEquals(new BigDecimal("95000"), pricing.getTotal());
    }

    @Test
    @DisplayName("음수 값이 있으면 예외가 발생한다")
    void throwsExceptionWhenNegativeValues() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(new BigDecimal("-100"), BigDecimal.ZERO, BigDecimal.ZERO, "KRW"));
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(new BigDecimal("100"), new BigDecimal("-10"), BigDecimal.ZERO, "KRW"));
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(new BigDecimal("100"), BigDecimal.ZERO, new BigDecimal("-10"), "KRW"));
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(null, BigDecimal.ZERO, BigDecimal.ZERO, "KRW"));
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(new BigDecimal("100"), null, BigDecimal.ZERO, "KRW"));
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(new BigDecimal("100"), BigDecimal.ZERO, null, "KRW"));
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(new BigDecimal("100"), BigDecimal.ZERO, BigDecimal.ZERO, null));
    }

    @Test
    @DisplayName("통화가 빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenCurrencyIsEmpty() {
        assertThrows(IllegalArgumentException.class,
            () -> new Pricing(new BigDecimal("100"), BigDecimal.ZERO, BigDecimal.ZERO, ""));
    }

    @Test
    @DisplayName("같은 값을 가진 Pricing은 동일하다")
    void testEquality() {
        // given
        Pricing pricing1 = new Pricing(
            new BigDecimal("100000"),
            new BigDecimal("3000"),
            new BigDecimal("5000"),
            "KRW"
        );
        Pricing pricing2 = new Pricing(
            new BigDecimal("100000"),
            new BigDecimal("3000"),
            new BigDecimal("5000"),
            "KRW"
        );
        
        // then
        assertEquals(pricing1, pricing2);
        assertEquals(pricing1.hashCode(), pricing2.hashCode());
    }
}


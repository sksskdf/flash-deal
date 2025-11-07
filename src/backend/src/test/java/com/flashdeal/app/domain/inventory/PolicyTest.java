package com.flashdeal.app.domain.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Policy Value Object 테스트")
class PolicyTest {

    @Test
    @DisplayName("유효한 정책으로 Policy를 생성할 수 있다")
    void createPolicyWithValidValues() {
        // when
        Policy policy = new Policy(50, 600, 5);
        
        // then
        assertNotNull(policy);
        assertEquals(50, policy.safetyStock());
        assertEquals(600, policy.reservationTimeout());
        assertEquals(5, policy.maxPurchasePerUser());
    }

    @Test
    @DisplayName("안전 재고가 음수이면 예외가 발생한다")
    void throwsExceptionWhenSafetyStockIsNegative() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Policy(-1, 600, 5));
    }

    @Test
    @DisplayName("예약 제한 시간이 0 이하이면 예외가 발생한다")
    void throwsExceptionWhenReservationTimeoutIsZeroOrNegative() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Policy(50, 0, 5));
        assertThrows(IllegalArgumentException.class, 
            () -> new Policy(50, -1, 5));
    }

    @Test
    @DisplayName("1인 최대 구매량이 0 이하이면 예외가 발생한다")
    void throwsExceptionWhenMaxPurchasePerUserIsZeroOrNegative() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Policy(50, 600, 0));
        assertThrows(IllegalArgumentException.class, 
            () -> new Policy(50, 600, -1));
    }

    @Test
    @DisplayName("재고가 안전 재고량보다 적으면 품절 임박 상태다")
    void isLowStockWhenAvailableIsBelowSafetyStock() {
        // given
        Policy policy = new Policy(50, 600, 5);
        
        // when & then
        assertTrue(policy.isLowStock(49));
        assertTrue(policy.isLowStock(0));
    }

    @Test
    @DisplayName("재고가 안전 재고량 이상이면 품절 임박이 아니다")
    void isNotLowStockWhenAvailableIsAboveSafetyStock() {
        // given
        Policy policy = new Policy(50, 600, 5);
        
        // when & then
        assertFalse(policy.isLowStock(50));
        assertFalse(policy.isLowStock(100));
    }

    @Test
    @DisplayName("구매 가능 수량인지 확인할 수 있다")
    void canValidatePurchaseQuantity() {
        // given
        Policy policy = new Policy(50, 600, 5);
        
        // when & then
        assertTrue(policy.isValidPurchaseQuantity(1));
        assertTrue(policy.isValidPurchaseQuantity(5));
        assertFalse(policy.isValidPurchaseQuantity(6));
        assertFalse(policy.isValidPurchaseQuantity(0));
        assertFalse(policy.isValidPurchaseQuantity(-1));
    }

    @Test
    @DisplayName("기본 정책을 생성할 수 있다")
    void createDefaultPolicy() {
        // when
        Policy policy = Policy.defaultPolicy();
        
        // then
        assertNotNull(policy);
        assertEquals(10, policy.safetyStock());
        assertEquals(600, policy.reservationTimeout());
        assertEquals(10, policy.maxPurchasePerUser());
    }

    @Test
    @DisplayName("같은 정책은 동일하다")
    void testEquality() {
        // given
        Policy policy1 = new Policy(50, 600, 5);
        Policy policy2 = new Policy(50, 600, 5);
        
        // then
        assertEquals(policy1, policy2);
        assertEquals(policy1.hashCode(), policy2.hashCode());
    }

    @Test
    @DisplayName("다른 정책은 다르다")
    void testInequality() {
        // given
        Policy policy1 = new Policy(50, 600, 5);
        Policy policy2 = new Policy(40, 600, 5);
        
        // then
        assertNotEquals(policy1, policy2);
    }
}


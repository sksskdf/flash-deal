package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderId Value Object 테스트")
class OrderIdTest {

    @Test
    @DisplayName("유효한 UUID로 OrderId를 생성할 수 있다")
    void createOrderIdWithValidUUID() {
        // given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";
        
        // when
        OrderId orderId = new OrderId(validUuid);
        
        // then
        assertNotNull(orderId);
        assertEquals(validUuid, orderId.value());
    }

    @Test
    @DisplayName("null 값으로 OrderId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
    }

    @Test
    @DisplayName("빈 문자열로 OrderId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(""));
    }

    @Test
    @DisplayName("새로운 OrderId를 생성할 수 있다")
    void generateNewOrderId() {
        // when
        OrderId orderId = OrderId.generate();
        
        // then
        assertNotNull(orderId);
        assertNotNull(orderId.value());
    }

    @Test
    @DisplayName("같은 값을 가진 OrderId는 동일하다")
    void testEquality() {
        // given
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        
        // when
        OrderId orderId1 = new OrderId(uuid);
        OrderId orderId2 = new OrderId(uuid);
        
        // then
        assertEquals(orderId1, orderId2);
        assertEquals(orderId1.hashCode(), orderId2.hashCode());
    }
}


package com.flashdeal.app.domain.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InventoryId Value Object 테스트")
class InventoryIdTest {

    @Test
    @DisplayName("유효한 UUID로 InventoryId를 생성할 수 있다")
    void createInventoryIdWithValidUUID() {
        // given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";
        
        // when
        InventoryId inventoryId = new InventoryId(validUuid);
        
        // then
        assertNotNull(inventoryId);
        assertEquals(validUuid, inventoryId.getValue());
    }

    @Test
    @DisplayName("null 값으로 InventoryId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsNull() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> new InventoryId(null));
    }

    @Test
    @DisplayName("빈 문자열로 InventoryId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsEmpty() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> new InventoryId(""));
        assertThrows(IllegalArgumentException.class, () -> new InventoryId("   "));
    }

    @Test
    @DisplayName("새로운 InventoryId를 생성할 수 있다")
    void generateNewInventoryId() {
        // when
        InventoryId inventoryId = InventoryId.generate();
        
        // then
        assertNotNull(inventoryId);
        assertNotNull(inventoryId.getValue());
        assertFalse(inventoryId.getValue().isEmpty());
    }

    @Test
    @DisplayName("같은 값을 가진 InventoryId는 동일하다")
    void testEquality() {
        // given
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        InventoryId inventoryId1 = new InventoryId(uuid);
        InventoryId inventoryId2 = new InventoryId(uuid);
        
        // then
        assertEquals(inventoryId1, inventoryId2);
        assertEquals(inventoryId1.hashCode(), inventoryId2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 InventoryId는 다르다")
    void testInequality() {
        // given
        InventoryId inventoryId1 = new InventoryId("123e4567-e89b-12d3-a456-426614174000");
        InventoryId inventoryId2 = new InventoryId("223e4567-e89b-12d3-a456-426614174000");
        
        // then
        assertNotEquals(inventoryId1, inventoryId2);
    }
}


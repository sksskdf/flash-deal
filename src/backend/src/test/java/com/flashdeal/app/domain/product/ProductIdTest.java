package com.flashdeal.app.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductId Value Object 테스트")
class ProductIdTest {

    @Test
    @DisplayName("유효한 UUID로 ProductId를 생성할 수 있다")
    void createProductIdWithValidUUID() {
        // given
        String validUuid = "123e4567-e89b-12d3-a456-426614174000";
        
        // when
        ProductId productId = new ProductId(validUuid);
        
        // then
        assertNotNull(productId);
        assertEquals(validUuid, productId.value());
    }

    @Test
    @DisplayName("null 값으로 ProductId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsNull() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> new ProductId(null));
    }

    @Test
    @DisplayName("빈 문자열로 ProductId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsEmpty() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> new ProductId(""));
        assertThrows(IllegalArgumentException.class, () -> new ProductId("   "));
    }

    @Test
    @DisplayName("새로운 ProductId를 생성할 수 있다")
    void generateNewProductId() {
        // when
        ProductId productId = ProductId.generate();
        
        // then
        assertNotNull(productId);
        assertNotNull(productId.value());
        assertFalse(productId.value().isEmpty());
    }

    @Test
    @DisplayName("같은 값을 가진 ProductId는 동일하다")
    void testEquality() {
        // given
        String uuid = "123e4567-e89b-12d3-a456-426614174000";
        ProductId productId1 = new ProductId(uuid);
        ProductId productId2 = new ProductId(uuid);
        
        // then
        assertEquals(productId1, productId2);
        assertEquals(productId1.hashCode(), productId2.hashCode());
    }

    @Test
    @DisplayName("다른 값을 가진 ProductId는 다르다")
    void testInequality() {
        // given
        ProductId productId1 = new ProductId("123e4567-e89b-12d3-a456-426614174000");
        ProductId productId2 = new ProductId("223e4567-e89b-12d3-a456-426614174000");
        
        // then
        assertNotEquals(productId1, productId2);
    }
}


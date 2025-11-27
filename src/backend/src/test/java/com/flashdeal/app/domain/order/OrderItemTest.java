package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;

import com.flashdeal.app.domain.product.Price;
import com.flashdeal.app.domain.product.ProductId;

import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderItem Entity 테스트")
class OrderItemTest {

    @Test
    @DisplayName("유효한 값으로 OrderItem을 생성할 수 있다")
    void createOrderItemWithValidValues() {
        // given
        ProductId productId = ProductId.generate();
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot = createSnapshot(price);
        
        // when
        OrderItem orderItem = new OrderItem(productId, snapshot, 2);
        
        // then
        assertNotNull(orderItem);
        assertEquals(productId, orderItem.productId());
        assertEquals(snapshot, orderItem.snapshot());
        assertEquals(2, orderItem.quantity());
        assertEquals(new BigDecimal("160000"), orderItem.snapshot().price().sale().multiply(BigDecimal.valueOf(orderItem.quantity())));
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot = createSnapshot(price);
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(null, snapshot, 1));
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(ProductId.generate(), null, 1));
    }

    @Test
    @DisplayName("수량이 0 이하이면 예외가 발생한다")
    void throwsExceptionWhenQuantityIsZeroOrNegative() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot = createSnapshot(price);
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(ProductId.generate(), snapshot, 0));
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItem(ProductId.generate(), snapshot, -1));
    }

    @Test
    @DisplayName("소계는 할인가 × 수량으로 계산된다")
    void subtotalCalculatedCorrectly() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot = createSnapshot(price);
        
        // when
        OrderItem orderItem = new OrderItem(ProductId.generate(), snapshot, 3);
        
        // then
        assertEquals(new BigDecimal("240000"), orderItem.snapshot().price().sale().multiply(BigDecimal.valueOf(orderItem.quantity())));
    }

    @Test
    @DisplayName("수량을 변경할 수 있다")
    void canUpdateQuantity() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot = createSnapshot(price);
        OrderItem orderItem = new OrderItem(ProductId.generate(), snapshot, 2);
        
        // when
        OrderItem updated = orderItem.updateQuantity(5);
        
        // then
        assertEquals(5, updated.quantity());
        assertEquals(new BigDecimal("400000"), updated.snapshot().price().sale().multiply(BigDecimal.valueOf(updated.quantity())));
        // 원본은 변경되지 않음
        assertEquals(2, orderItem.quantity());
    }

    @Test
    @DisplayName("수량을 0 이하로 변경하면 예외가 발생한다")
    void throwsExceptionWhenUpdateQuantityToZeroOrNegative() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot = createSnapshot(price);
        OrderItem orderItem = new OrderItem(ProductId.generate(), snapshot, 2);
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> orderItem.updateQuantity(0));
        assertThrows(IllegalArgumentException.class,
            () -> orderItem.updateQuantity(-1));
    }

    private Snapshot createSnapshot(Price price) {
        Map<String, Object> options = new HashMap<>();
        options.put("color", "black");
        return new Snapshot(
            "AirPods Pro",
            "https://example.com/image.jpg",
            price,
            options
        );
    }
}


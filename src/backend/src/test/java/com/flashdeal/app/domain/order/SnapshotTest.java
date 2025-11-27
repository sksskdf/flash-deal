package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;

import com.flashdeal.app.domain.product.Price;

import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Snapshot Value Object 테스트")
class SnapshotTest {

    @Test
    @DisplayName("유효한 값으로 Snapshot을 생성할 수 있다")
    void createSnapshotWithValidValues() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Map<String, Object> options = new HashMap<>();
        options.put("color", "black");
        
        // when
        Snapshot snapshot = new Snapshot(
            "AirPods Pro",
            "https://example.com/image.jpg",
            price,
            options
        );
        
        // then
        assertNotNull(snapshot);
        assertEquals("AirPods Pro", snapshot.title());
        assertEquals("https://example.com/image.jpg", snapshot.image());
        assertEquals(price, snapshot.price());
        assertEquals("black", snapshot.selectedOptions().get("color"));
    }

    @Test
    @DisplayName("선택 옵션은 선택 사항이다")
    void selectedOptionsCanBeEmpty() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        
        // when
        Snapshot snapshot = new Snapshot(
            "AirPods Pro",
            "https://example.com/image.jpg",
            price,
            new HashMap<>()
        );
        
        // then
        assertTrue(snapshot.getSelectedOptions().isEmpty());
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Map<String, Object> options = new HashMap<>();
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Snapshot(null, "image.jpg", price, options));
        assertThrows(IllegalArgumentException.class,
            () -> new Snapshot("Title", null, price, options));
        assertThrows(IllegalArgumentException.class,
            () -> new Snapshot("Title", "image.jpg", null, options));
        assertThrows(IllegalArgumentException.class,
            () -> new Snapshot("Title", "image.jpg", price, null));
    }

    @Test
    @DisplayName("제목이 빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenTitleIsEmpty() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Snapshot("", "image.jpg", price, new HashMap<>()));
    }

    @Test
    @DisplayName("Snapshot은 불변이다")
    void snapshotIsImmutable() {
        // given
        Price price = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Map<String, Object> options = new HashMap<>();
        options.put("color", "black");
        Snapshot snapshot = new Snapshot("Title", "image.jpg", price, options);
        
        // when
        options.put("color", "white");
        options.put("size", "large");
        
        // then
        assertEquals("black", snapshot.getSelectedOptions().get("color"));
        assertFalse(snapshot.getSelectedOptions().containsKey("size"));
    }
}


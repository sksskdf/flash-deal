package com.flashdeal.app.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Specs Value Object 테스트")
class SpecsTest {

    @Test
    @DisplayName("유효한 필드로 Specs를 생성할 수 있다")
    void createSpecsWithValidFields() {
        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("battery", "30h");
        fields.put("bluetooth", "5.0");
        fields.put("weight", 250);
        
        // when
        Specs specs = new Specs(fields);
        
        // then
        assertNotNull(specs);
        assertEquals("30h", specs.get("battery"));
        assertEquals("5.0", specs.get("bluetooth"));
        assertEquals(250, specs.get("weight"));
    }

    @Test
    @DisplayName("빈 필드로 Specs를 생성할 수 있다")
    void createSpecsWithEmptyFields() {
        // given
        Map<String, Object> fields = new HashMap<>();
        
        // when
        Specs specs = new Specs(fields);
        
        // then
        assertNotNull(specs);
    }

    @Test
    @DisplayName("null로 Specs를 생성하면 예외가 발생한다")
    void throwsExceptionWhenFieldsIsNull() {
        // when & then
        assertThrows(IllegalArgumentException.class, () -> new Specs(null));
    }

    @Test
    @DisplayName("필드가 존재하는지 확인할 수 있다")
    void hasReturnsTrueWhenFieldExists() {
        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("battery", "30h");
        Specs specs = new Specs(fields);
        
        // when & then
        assertTrue(specs.has("battery"));
        assertFalse(specs.has("nonexistent"));
    }

    @Test
    @DisplayName("존재하지 않는 필드를 가져오면 null을 반환한다")
    void getReturnsNullWhenFieldDoesNotExist() {
        // given
        Specs specs = new Specs(new HashMap<>());
        
        // when
        Object value = specs.get("nonexistent");
        
        // then
        assertNull(value);
    }

    @Test
    @DisplayName("같은 필드를 가진 Specs는 동일하다")
    void testEquality() {
        // given
        Map<String, Object> fields1 = new HashMap<>();
        fields1.put("battery", "30h");
        fields1.put("bluetooth", "5.0");
        
        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("battery", "30h");
        fields2.put("bluetooth", "5.0");
        
        Specs specs1 = new Specs(fields1);
        Specs specs2 = new Specs(fields2);
        
        // then
        assertEquals(specs1, specs2);
        assertEquals(specs1.hashCode(), specs2.hashCode());
    }

    @Test
    @DisplayName("다른 필드를 가진 Specs는 다르다")
    void testInequality() {
        // given
        Map<String, Object> fields1 = new HashMap<>();
        fields1.put("battery", "30h");
        
        Map<String, Object> fields2 = new HashMap<>();
        fields2.put("battery", "40h");
        
        Specs specs1 = new Specs(fields1);
        Specs specs2 = new Specs(fields2);
        
        // then
        assertNotEquals(specs1, specs2);
    }

    @Test
    @DisplayName("Specs는 불변이다 - 원본 맵을 변경해도 영향받지 않는다")
    void specsIsImmutable() {
        // given
        Map<String, Object> fields = new HashMap<>();
        fields.put("battery", "30h");
        Specs specs = new Specs(fields);
        
        // when
        fields.put("battery", "40h");
        fields.put("newField", "value");
        
        // then
        assertEquals("30h", specs.get("battery"));
        assertFalse(specs.has("newField"));
    }
}


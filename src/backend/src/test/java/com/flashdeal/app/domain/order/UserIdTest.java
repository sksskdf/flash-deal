package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserId Value Object 테스트")
class UserIdTest {

    @Test
    @DisplayName("유효한 문자열로 UserId를 생성할 수 있다")
    void createUserIdWithValidString() {
        // given
        String value = "user123";
        
        // when
        UserId userId = new UserId(value);
        
        // then
        assertNotNull(userId);
        assertEquals(value, userId.value());
    }

    @Test
    @DisplayName("null 값으로 UserId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(null));
    }

    @Test
    @DisplayName("빈 문자열로 UserId를 생성하면 예외가 발생한다")
    void throwsExceptionWhenValueIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(""));
    }

    @Test
    @DisplayName("같은 값을 가진 UserId는 동일하다")
    void testEquality() {
        // given
        String value = "user123";
        
        // when
        UserId userId1 = new UserId(value);
        UserId userId2 = new UserId(value);
        
        // then
        assertEquals(userId1, userId2);
        assertEquals(userId1.hashCode(), userId2.hashCode());
    }
}


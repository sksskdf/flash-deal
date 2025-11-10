package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Recipient Value Object 테스트")
class RecipientTest {

    @Test
    @DisplayName("유효한 값으로 Recipient를 생성할 수 있다")
    void createRecipientWithValidValues() {
        // when
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        
        // then
        assertNotNull(recipient);
        assertEquals("홍길동", recipient.name());
        assertEquals("+82-10-1234-5678", recipient.phone());
    }

    @Test
    @DisplayName("이름이 null이면 예외가 발생한다")
    void throwsExceptionWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Recipient(null, "+82-10-1234-5678"));
    }

    @Test
    @DisplayName("이름이 빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenNameIsEmpty() {
        assertThrows(IllegalArgumentException.class,
            () -> new Recipient("", "+82-10-1234-5678"));
    }

    @Test
    @DisplayName("전화번호가 null이면 예외가 발생한다")
    void throwsExceptionWhenPhoneIsNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Recipient("홍길동", null));
    }

    @Test
    @DisplayName("전화번호가 빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenPhoneIsEmpty() {
        assertThrows(IllegalArgumentException.class,
            () -> new Recipient("홍길동", ""));
    }

    @Test
    @DisplayName("같은 값을 가진 Recipient는 동일하다")
    void testEquality() {
        // given
        Recipient recipient1 = new Recipient("홍길동", "+82-10-1234-5678");
        Recipient recipient2 = new Recipient("홍길동", "+82-10-1234-5678");
        
        // then
        assertEquals(recipient1, recipient2);
        assertEquals(recipient1.hashCode(), recipient2.hashCode());
    }
}


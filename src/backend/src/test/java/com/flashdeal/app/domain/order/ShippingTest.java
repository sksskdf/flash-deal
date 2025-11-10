package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Shipping Value Object 테스트")
class ShippingTest {

    @Test
    @DisplayName("유효한 값으로 Shipping을 생성할 수 있다")
    void createShippingWithValidValues() {
        // given
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        Address address = new Address("테헤란로 427", "서울", "06158", "KR");
        
        // when
        Shipping shipping = new Shipping("Standard", recipient, address, "문 앞에 놔주세요");
        
        // then
        assertNotNull(shipping);
        assertEquals("Standard", shipping.method());
        assertEquals(recipient, shipping.recipient());
        assertEquals(address, shipping.address());
        assertEquals("문 앞에 놔주세요", shipping.instructions());
    }

    @Test
    @DisplayName("요청사항은 선택 사항이다")
    void instructionsCanBeNull() {
        // given
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        Address address = new Address("테헤란로 427", "서울", "06158", "KR");
        
        // when
        Shipping shipping = new Shipping("Standard", recipient, address, null);
        
        // then
        assertNull(shipping.instructions());
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        Address address = new Address("테헤란로 427", "서울", "06158", "KR");
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Shipping(null, recipient, address, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Shipping("Standard", null, address, null));
        assertThrows(IllegalArgumentException.class,
            () -> new Shipping("Standard", recipient, null, null));
    }

    @Test
    @DisplayName("배송 방법이 빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenMethodIsEmpty() {
        // given
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        Address address = new Address("테헤란로 427", "서울", "06158", "KR");
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Shipping("", recipient, address, null));
    }
}


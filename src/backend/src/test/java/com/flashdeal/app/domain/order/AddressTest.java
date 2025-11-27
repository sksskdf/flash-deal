package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Address Value Object 테스트")
class AddressTest {

    @Test
    @DisplayName("유효한 값으로 Address를 생성할 수 있다")
    void createAddressWithValidValues() {
        // when
        Address address = new Address(
            "테헤란로 427",
            "서울",
            "06158",
            "KR"
        );
        
        // then
        assertNotNull(address);
        assertEquals("테헤란로 427", address.street());
        assertEquals("서울", address.city());
        assertEquals("06158", address.zipCode());
        assertEquals("KR", address.country());
    }

    @Test
    @DisplayName("모든 필드가 필수다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Address(null, "서울", "06158", "KR"));
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", null, "06158", "KR"));
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", "서울", null, "KR"));
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", "서울", "06158", null));
    }

    @Test
    @DisplayName("빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenFieldsAreEmpty() {
        assertThrows(IllegalArgumentException.class,
            () -> new Address("", "서울", "06158", "KR"));
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", "", "06158", "KR"));
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", "서울", "", "KR"));
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", "서울", "06158", ""));
    }

    @Test
    @DisplayName("국가 코드는 2자리여야 한다")
    void throwsExceptionWhenCountryCodeIsNot2Characters() {
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", "서울", "06158", "K"));
        assertThrows(IllegalArgumentException.class,
            () -> new Address("테헤란로 427", "서울", "06158", "KOR"));
    }

    @Test
    @DisplayName("같은 값을 가진 Address는 동일하다")
    void testEquality() {
        // given
        Address address1 = new Address("테헤란로 427", "서울", "06158", "KR");
        Address address2 = new Address("테헤란로 427", "서울", "06158", "KR");
        
        // then
        assertEquals(address1, address2);
        assertEquals(address1.hashCode(), address2.hashCode());
    }
}


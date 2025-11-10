package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Payment Value Object 테스트")
class PaymentTest {

    @Test
    @DisplayName("유효한 값으로 Payment를 생성할 수 있다")
    void createPaymentWithValidValues() {
        // when
        Payment payment = new Payment(
            "CreditCard",
            PaymentStatus.PENDING,
            null,
            "Stripe"
        );
        
        // then
        assertNotNull(payment);
        assertEquals("CreditCard", payment.method());
        assertEquals(PaymentStatus.PENDING, payment.status());
        assertNull(payment.transactionId());
        assertEquals("Stripe", payment.gateway());
    }

    @Test
    @DisplayName("결제 완료 시 트랜잭션 ID가 필요하다")
    void completedPaymentRequiresTransactionId() {
        // when
        Payment payment = new Payment(
            "CreditCard",
            PaymentStatus.COMPLETED,
            "txn_12345",
            "Stripe"
        );
        
        // then
        assertEquals("txn_12345", payment.transactionId());
    }

    @Test
    @DisplayName("결제 완료인데 트랜잭션 ID가 없으면 예외가 발생한다")
    void throwsExceptionWhenCompletedWithoutTransactionId() {
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Payment("CreditCard", PaymentStatus.COMPLETED, null, "Stripe"));
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        assertThrows(IllegalArgumentException.class,
            () -> new Payment(null, PaymentStatus.PENDING, null, "Stripe"));
        assertThrows(IllegalArgumentException.class,
            () -> new Payment("CreditCard", null, null, "Stripe"));
    }

    @Test
    @DisplayName("결제를 완료할 수 있다")
    void canCompletePayment() {
        // given
        Payment payment = new Payment("CreditCard", PaymentStatus.PENDING, null, "Stripe");
        
        // when
        Payment completed = payment.complete("txn_12345");
        
        // then
        assertEquals(PaymentStatus.COMPLETED, completed.status());
        assertEquals("txn_12345", completed.transactionId());
    }

    @Test
    @DisplayName("결제를 실패 처리할 수 있다")
    void canFailPayment() {
        // given
        Payment payment = new Payment("CreditCard", PaymentStatus.PENDING, null, "Stripe");
        
        // when
        Payment failed = payment.fail();
        
        // then
        assertEquals(PaymentStatus.FAILED, failed.status());
    }
}


package com.flashdeal.app.domain.order;

import static com.flashdeal.app.domain.validator.Validator.validateNotEmpty;
import static com.flashdeal.app.domain.validator.Validator.validateNotNull;

/**
 * 결제 정보 Value Object
 */
public record Payment(
        String method,
        PaymentStatus status,
        String transactionId,
        String gateway) {
    public Payment {
        validateNotEmpty(method, "Method cannot be null or empty");
        validateNotNull(status, "Status cannot be null");

        if (status == PaymentStatus.COMPLETED && (transactionId == null || transactionId.trim().isEmpty())) {
            throw new IllegalArgumentException("TransactionId is required when status is COMPLETED");
        }
    }

    /**
     * 결제 완료 처리
     */
    public Payment complete(String transactionId) {
        return new Payment(method, PaymentStatus.COMPLETED, transactionId, gateway);
    }

    /**
     * 결제 실패 처리
     */
    public Payment fail() {
        return new Payment(method, PaymentStatus.FAILED, transactionId, gateway);
    }

    /**
     * 환불 처리
     */
    public Payment refund() {
        return new Payment(method, PaymentStatus.REFUNDED, transactionId, gateway);
    }
}

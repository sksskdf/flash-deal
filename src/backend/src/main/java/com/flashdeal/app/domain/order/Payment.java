package com.flashdeal.app.domain.order;

/**
 * 결제 정보 Value Object
 */
public record Payment(
    String method,
    PaymentStatus status,
    String transactionId,
    String gateway
) {
    public Payment {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
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


package com.flashdeal.app.domain.order;

import java.util.Objects;

/**
 * 결제 정보 Value Object
 */
public final class Payment {
    
    private final String method;
    private final PaymentStatus status;
    private final String transactionId;
    private final String gateway;

    public Payment(String method, PaymentStatus status, String transactionId, String gateway) {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
        
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        if (status == PaymentStatus.COMPLETED && (transactionId == null || transactionId.trim().isEmpty())) {
            throw new IllegalArgumentException("TransactionId is required when status is COMPLETED");
        }
        
        this.method = method;
        this.status = status;
        this.transactionId = transactionId;
        this.gateway = gateway;
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

    public String getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getGateway() {
        return gateway;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(method, payment.method) &&
               status == payment.status &&
               Objects.equals(transactionId, payment.transactionId) &&
               Objects.equals(gateway, payment.gateway);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, status, transactionId, gateway);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "method='" + method + '\'' +
                ", status=" + status +
                ", transactionId='" + transactionId + '\'' +
                ", gateway='" + gateway + '\'' +
                '}';
    }
}


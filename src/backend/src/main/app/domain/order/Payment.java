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

    /**
     * 결제 게이트웨이 정보 (임시 구현)
     */
    public Gateway getGatewayInfo() {
        return new Gateway("TOSS", "TXN123456", "https://receipt.example.com",
                java.time.ZonedDateTime.now());
    }

    /**
     * 카드 정보 (임시 구현)
     */
    public Card getCard() {
        return new Card("****1234", "VISA", 12, 2025);
    }

    /**
     * 환불 정보 (임시 구현)
     */
    public Refund getRefund() {
        return null; // 환불이 없는 경우
    }

    /**
     * 할부 정보 (임시 구현)
     */
    public Installment getInstallment() {
        return null; // 할부가 없는 경우
    }

    /**
     * 카드 내부 클래스
     */
    public static class Card {
        private final String last4;
        private final String brand;
        private final int expiryMonth;
        private final int expiryYear;

        public Card(String last4, String brand, int expiryMonth, int expiryYear) {
            this.last4 = last4;
            this.brand = brand;
            this.expiryMonth = expiryMonth;
            this.expiryYear = expiryYear;
        }

        public String getLast4() {
            return last4;
        }

        public String getBrand() {
            return brand;
        }

        public int getExpiryMonth() {
            return expiryMonth;
        }

        public int getExpiryYear() {
            return expiryYear;
        }
    }

    /**
     * 환불 내부 클래스
     */
    public static class Refund {
        private final String id;
        private final java.math.BigDecimal amount;
        private final String reason;
        private final String status;
        private final java.time.ZonedDateTime requestedAt;
        private final java.time.ZonedDateTime processedAt;

        public Refund(String id, java.math.BigDecimal amount, String reason, String status,
                java.time.ZonedDateTime requestedAt, java.time.ZonedDateTime processedAt) {
            this.id = id;
            this.amount = amount;
            this.reason = reason;
            this.status = status;
            this.requestedAt = requestedAt;
            this.processedAt = processedAt;
        }

        public String getId() {
            return id;
        }

        public java.math.BigDecimal getAmount() {
            return amount;
        }

        public String getReason() {
            return reason;
        }

        public String getStatus() {
            return status;
        }

        public java.time.ZonedDateTime getRequestedAt() {
            return requestedAt;
        }

        public java.time.ZonedDateTime getProcessedAt() {
            return processedAt;
        }
    }

    /**
     * 할부 내부 클래스
     */
    public static class Installment {
        private final int count;
        private final java.math.BigDecimal amount;
        private final java.time.ZonedDateTime firstDue;
        private final java.time.ZonedDateTime lastDue;

        public Installment(int count, java.math.BigDecimal amount,
                java.time.ZonedDateTime firstDue, java.time.ZonedDateTime lastDue) {
            this.count = count;
            this.amount = amount;
            this.firstDue = firstDue;
            this.lastDue = lastDue;
        }

        public int getCount() {
            return count;
        }

        public java.math.BigDecimal getAmount() {
            return amount;
        }

        public java.time.ZonedDateTime getFirstDue() {
            return firstDue;
        }

        public java.time.ZonedDateTime getLastDue() {
            return lastDue;
        }
    }

    /**
     * 결제 게이트웨이 내부 클래스
     */
    public static class Gateway {
        private final String provider;
        private final String transactionId;
        private final String receiptUrl;
        private final java.time.ZonedDateTime chargedAt;

        public Gateway(String provider, String transactionId, String receiptUrl,
                java.time.ZonedDateTime chargedAt) {
            this.provider = provider;
            this.transactionId = transactionId;
            this.receiptUrl = receiptUrl;
            this.chargedAt = chargedAt;
        }

        public String getProvider() {
            return provider;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getReceiptUrl() {
            return receiptUrl;
        }

        public java.time.ZonedDateTime getChargedAt() {
            return chargedAt;
        }
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


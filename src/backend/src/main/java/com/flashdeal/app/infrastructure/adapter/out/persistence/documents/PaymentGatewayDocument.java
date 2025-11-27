package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;

public class PaymentGatewayDocument {
    private String provider;
    private String transactionId;
    private String receiptUrl;
    private Instant chargedAt;

    public PaymentGatewayDocument(String provider, String transactionId, String receiptUrl, Instant chargedAt) {
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

    public Instant getChargedAt() {
        return chargedAt;
    }
}
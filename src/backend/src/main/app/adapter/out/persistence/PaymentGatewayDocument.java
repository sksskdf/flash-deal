package com.flashdeal.app.adapter.out.persistence;

import java.time.ZonedDateTime;

/**
 * Payment Gateway MongoDB Document
 */
public class PaymentGatewayDocument {
    
    private String provider;
    private String transactionId;
    private String receiptUrl;
    private ZonedDateTime chargedAt;

    public PaymentGatewayDocument() {}

    public PaymentGatewayDocument(String provider, String transactionId, String receiptUrl, ZonedDateTime chargedAt) {
        this.provider = provider;
        this.transactionId = transactionId;
        this.receiptUrl = receiptUrl;
        this.chargedAt = chargedAt;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public ZonedDateTime getChargedAt() {
        return chargedAt;
    }

    public void setChargedAt(ZonedDateTime chargedAt) {
        this.chargedAt = chargedAt;
    }
}






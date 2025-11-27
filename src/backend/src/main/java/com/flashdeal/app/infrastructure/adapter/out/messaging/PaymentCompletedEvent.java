package com.flashdeal.app.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 결제 완료 이벤트
 */
public class PaymentCompletedEvent extends DomainEvent {
    
    @JsonProperty("payment")
    private final PaymentData payment;
    
    public PaymentCompletedEvent(String aggregateId, String correlationId, String causationId, 
                               Map<String, Object> metadata, PaymentData payment) {
        super("PaymentCompleted", "1.0", aggregateId, correlationId, causationId, metadata);
        this.payment = payment;
    }
    
    public PaymentData getPayment() { return payment; }
    
    public static class PaymentData {
        @JsonProperty("orderId")
        private final String orderId;
        
        @JsonProperty("amount")
        private final BigDecimal amount;
        
        @JsonProperty("currency")
        private final String currency;
        
        @JsonProperty("method")
        private final String method;
        
        @JsonProperty("transactionId")
        private final String transactionId;
        
        @JsonProperty("gateway")
        private final String gateway;
        
        public PaymentData(String orderId, BigDecimal amount, String currency, 
                          String method, String transactionId, String gateway) {
            this.orderId = orderId;
            this.amount = amount;
            this.currency = currency;
            this.method = method;
            this.transactionId = transactionId;
            this.gateway = gateway;
        }
        
        // Getters
        public String getOrderId() { return orderId; }
        public BigDecimal getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public String getMethod() { return method; }
        public String getTransactionId() { return transactionId; }
        public String getGateway() { return gateway; }
    }
}

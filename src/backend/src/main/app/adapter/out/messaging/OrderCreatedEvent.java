package com.flashdeal.app.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 주문 생성 이벤트
 */
public class OrderCreatedEvent extends DomainEvent {
    
    @JsonProperty("order")
    private final OrderData order;
    
    public OrderCreatedEvent(String aggregateId, String correlationId, String causationId, 
                           Map<String, Object> metadata, OrderData order) {
        super("OrderCreated", "1.0", aggregateId, correlationId, causationId, metadata);
        this.order = order;
    }
    
    public OrderData getOrder() { return order; }
    
    public static class OrderData {
        @JsonProperty("orderId")
        private final String orderId;
        
        @JsonProperty("orderNumber")
        private final String orderNumber;
        
        @JsonProperty("userId")
        private final String userId;
        
        @JsonProperty("items")
        private final List<OrderItemData> items;
        
        @JsonProperty("total")
        private final BigDecimal total;
        
        @JsonProperty("currency")
        private final String currency;
        
        public OrderData(String orderId, String orderNumber, String userId, 
                        List<OrderItemData> items, BigDecimal total, String currency) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.userId = userId;
            this.items = items;
            this.total = total;
            this.currency = currency;
        }
        
        // Getters
        public String getOrderId() { return orderId; }
        public String getOrderNumber() { return orderNumber; }
        public String getUserId() { return userId; }
        public List<OrderItemData> getItems() { return items; }
        public BigDecimal getTotal() { return total; }
        public String getCurrency() { return currency; }
    }
    
    public static class OrderItemData {
        @JsonProperty("productId")
        private final String productId;
        
        @JsonProperty("quantity")
        private final int quantity;
        
        @JsonProperty("unitPrice")
        private final BigDecimal unitPrice;
        
        public OrderItemData(String productId, int quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        // Getters
        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
    }
}

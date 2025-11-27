package com.flashdeal.app.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * 재고 예약 이벤트
 */
public class InventoryReservedEvent extends DomainEvent {
    
    @JsonProperty("inventory")
    private final InventoryData inventory;
    
    public InventoryReservedEvent(String aggregateId, String correlationId, String causationId, 
                                Map<String, Object> metadata, InventoryData inventory) {
        super("InventoryReserved", "1.0", aggregateId, correlationId, causationId, metadata);
        this.inventory = inventory;
    }
    
    public InventoryData getInventory() { return inventory; }
    
    public static class InventoryData {
        @JsonProperty("productId")
        private final String productId;
        
        @JsonProperty("quantity")
        private final int quantity;
        
        @JsonProperty("orderId")
        private final String orderId;
        
        @JsonProperty("expiresAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private final ZonedDateTime expiresAt;
        
        public InventoryData(String productId, int quantity, String orderId, ZonedDateTime expiresAt) {
            this.productId = productId;
            this.quantity = quantity;
            this.orderId = orderId;
            this.expiresAt = expiresAt;
        }
        
        // Getters
        public String getProductId() { return productId; }
        public int getQuantity() { return quantity; }
        public String getOrderId() { return orderId; }
        public ZonedDateTime getExpiresAt() { return expiresAt; }
    }
}

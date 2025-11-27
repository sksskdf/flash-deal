package com.flashdeal.app.domain.order;

/**
 * 배송 정보 Value Object
 */
public record Shipping(
        String method,
        Recipient recipient,
        Address address,
        String instructions) {
    public Shipping {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
        
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient cannot be null");
        }
        
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
    }
}


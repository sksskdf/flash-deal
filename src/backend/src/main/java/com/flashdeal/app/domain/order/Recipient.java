package com.flashdeal.app.domain.order;

/**
 * 수령인 Value Object
 */
public record Recipient(
    String name,
    String phone
) {
    public Recipient {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
    }
}


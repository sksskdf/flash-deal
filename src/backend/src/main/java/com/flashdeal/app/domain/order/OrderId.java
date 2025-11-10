package com.flashdeal.app.domain.order;

import java.util.UUID;

public record OrderId(String value) {
    public OrderId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("OrderId value cannot be null or empty");
        }
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }
}


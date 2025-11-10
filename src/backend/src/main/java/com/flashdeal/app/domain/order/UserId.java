package com.flashdeal.app.domain.order;

import java.util.UUID;

/**
 * 사용자 식별자 Value Object
 */
public record UserId(String value) {
    public UserId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId value cannot be null or empty");
        }
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
}


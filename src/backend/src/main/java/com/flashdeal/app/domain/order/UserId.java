package com.flashdeal.app.domain.order;

import static com.flashdeal.app.domain.validator.Validator.validateNotEmpty;

import java.util.UUID;

/**
 * 사용자 식별자 Value Object
 */
public record UserId(String value) {
    public UserId {
        validateNotEmpty(value, "UserId value cannot be null or empty");
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }
}

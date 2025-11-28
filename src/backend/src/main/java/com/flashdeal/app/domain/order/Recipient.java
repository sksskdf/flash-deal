package com.flashdeal.app.domain.order;

import static com.flashdeal.app.domain.validator.Validator.validateNotEmpty;

/**
 * 수령인 Value Object
 */
public record Recipient(
        String name,
        String phone) {
    public Recipient {
        validateNotEmpty(name, "Name cannot be null or empty");
        validateNotEmpty(phone, "Phone cannot be null or empty");
    }
}

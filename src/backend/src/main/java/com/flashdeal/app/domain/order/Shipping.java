package com.flashdeal.app.domain.order;

import static com.flashdeal.app.domain.validator.Validator.validateNotEmpty;
import static com.flashdeal.app.domain.validator.Validator.validateNotNull;

/**
 * 배송 정보 Value Object
 */
public record Shipping(
        String method,
        Recipient recipient,
        Address address,
        String instructions) {
    public Shipping {
        validateNotEmpty(method, "Method cannot be null or empty");
        validateNotNull(recipient, "Recipient cannot be null");
        validateNotNull(address, "Address cannot be null");
    }
}

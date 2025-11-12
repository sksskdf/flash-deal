package com.flashdeal.app.domain.product;

import java.util.UUID;

public record ProductId(String value) {
    public ProductId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ProductId value cannot be null or empty");
        }
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }
}


package com.flashdeal.app.domain.inventory;

import java.util.UUID;

public record InventoryId(String value) {
    
    public InventoryId {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("InventoryId value cannot be null or empty");
        }
    }

    public static InventoryId generate() {
        return new InventoryId(UUID.randomUUID().toString());
    }
}


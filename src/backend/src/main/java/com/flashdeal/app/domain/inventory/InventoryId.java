package com.flashdeal.app.domain.inventory;

import java.util.Objects;
import java.util.UUID;

public final class InventoryId {
    
    private final String value;

    public InventoryId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("InventoryId value cannot be null or empty");
        }
        this.value = value;
    }

    public static InventoryId generate() {
        return new InventoryId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryId that = (InventoryId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "InventoryId{" + value + '}';
    }
}


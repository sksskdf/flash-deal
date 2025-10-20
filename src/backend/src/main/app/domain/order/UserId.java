package com.flashdeal.app.domain.order;

import java.util.Objects;
import java.util.UUID;

/**
 * 사용자 식별자 Value Object
 */
public final class UserId {
    
    private final String value;

    public UserId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("UserId value cannot be null or empty");
        }
        this.value = value;
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserId{" + value + '}';
    }
}


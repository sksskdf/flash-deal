package com.flashdeal.app.domain.order;

import java.util.Objects;
import java.util.UUID;

/**
 * Order의 식별자 Value Object
 */
public final class OrderId {
    
    private final String value;

    public OrderId(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("OrderId value cannot be null or empty");
        }
        this.value = value;
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "OrderId{" + value + '}';
    }
}


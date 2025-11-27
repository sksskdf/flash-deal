package com.flashdeal.app.domain.inventory;

public record Quantity(int value) {
    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }
}

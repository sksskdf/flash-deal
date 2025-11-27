package com.flashdeal.app.domain.inventory;

public record Policy(
        int safetyStock,
        int reservationTimeout,
        int maxPurchasePerUser) {

    public Policy {
        if (safetyStock < 0) {
            throw new IllegalArgumentException("Safety stock cannot be negative");
        }

        if (reservationTimeout <= 0) {
            throw new IllegalArgumentException("Reservation timeout must be positive");
        }

        if (maxPurchasePerUser <= 0) {
            throw new IllegalArgumentException("Max purchase per user must be positive");
        }
    }

    public static Policy defaultPolicy() {
        return new Policy(10, 600, 10);
    }

    public boolean isLowStock(Quantity available) {
        return available.value() < safetyStock;
    }

    public boolean isValidPurchaseQuantity(Quantity quantity) {
        return quantity.value() > 0 && quantity.value() <= maxPurchasePerUser;
    }
}

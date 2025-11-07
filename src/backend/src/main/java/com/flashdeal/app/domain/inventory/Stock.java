package com.flashdeal.app.domain.inventory;

public record Stock(
        int total,
        int reserved,
        int available,
        int sold) {

    public Stock {
        validateNonNegative(total, "Total");
        validateNonNegative(reserved, "Reserved");
        validateNonNegative(available, "Available");
        validateNonNegative(sold, "Sold");
        
        if (total != reserved + available + sold) {
            throw new IllegalArgumentException(
                String.format(
                    "Invariant violated: total(%d) must equal reserved(%d) + available(%d) + sold(%d) = %d",
                    total, reserved, available, sold, reserved + available + sold
                )
            );
        }
    }

    private void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
    }

    public static Stock initial(int total) {
        return new Stock(total, 0, total, 0);
    }

    public Stock decrease(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        if (quantity > available) {
            throw new IllegalArgumentException(
                String.format("Cannot decrease by %d: only %d available", quantity, available)
            );
        }
        
        return new Stock(
            total,
            reserved + quantity,
            available - quantity,
            sold
        );
    }

    public Stock confirm(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        if (quantity > reserved) {
            throw new IllegalArgumentException(
                String.format("Cannot confirm %d: only %d reserved", quantity, reserved)
            );
        }
        
        return new Stock(
            total,
            reserved - quantity,
            available,
            sold + quantity
        );
    }

    public Stock release(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        if (quantity > reserved) {
            throw new IllegalArgumentException(
                String.format("Cannot release %d: only %d reserved", quantity, reserved)
            );
        }
        
        return new Stock(
            total,
            reserved - quantity,
            available + quantity,
            sold
        );
    }

    public boolean isOutOfStock() {
        return available == 0;
    }
}


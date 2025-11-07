package com.flashdeal.app.domain.inventory;

import java.util.Objects;

public final class Stock {
    
    private final int total;
    private final int reserved;
    private final int available;
    private final int sold;

    public Stock(int total, int reserved, int available, int sold) {
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
        
        this.total = total;
        this.reserved = reserved;
        this.available = available;
        this.sold = sold;
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

    public int getTotal() {
        return total;
    }

    public int getReserved() {
        return reserved;
    }

    public int getAvailable() {
        return available;
    }

    public int getSold() {
        return sold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return total == stock.total &&
               reserved == stock.reserved &&
               available == stock.available &&
               sold == stock.sold;
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, reserved, available, sold);
    }

    @Override
    public String toString() {
        return "Stock{" +
                "total=" + total +
                ", reserved=" + reserved +
                ", available=" + available +
                ", sold=" + sold +
                '}';
    }
}


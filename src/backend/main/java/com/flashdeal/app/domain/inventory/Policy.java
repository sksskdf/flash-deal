package com.flashdeal.app.domain.inventory;

import java.util.Objects;

/**
 * 재고 정책 Value Object
 * 
 * 재고 운영 규칙 정의
 */
public final class Policy {
    
    private final int safetyStock;
    private final int reservationTimeout;
    private final int maxPurchasePerUser;

    public Policy(int safetyStock, int reservationTimeout, int maxPurchasePerUser) {
        if (safetyStock < 0) {
            throw new IllegalArgumentException("Safety stock cannot be negative");
        }
        
        if (reservationTimeout <= 0) {
            throw new IllegalArgumentException("Reservation timeout must be positive");
        }
        
        if (maxPurchasePerUser <= 0) {
            throw new IllegalArgumentException("Max purchase per user must be positive");
        }
        
        this.safetyStock = safetyStock;
        this.reservationTimeout = reservationTimeout;
        this.maxPurchasePerUser = maxPurchasePerUser;
    }

    /**
     * 기본 정책 생성
     */
    public static Policy defaultPolicy() {
        return new Policy(10, 600, 10);
    }

    /**
     * 재고가 안전 재고량 이하인지 확인
     */
    public boolean isLowStock(int available) {
        return available < safetyStock;
    }

    /**
     * 구매 가능한 수량인지 확인
     */
    public boolean isValidPurchaseQuantity(int quantity) {
        return quantity > 0 && quantity <= maxPurchasePerUser;
    }

    public int getSafetyStock() {
        return safetyStock;
    }

    public int getReservationTimeout() {
        return reservationTimeout;
    }

    public int getMaxPurchasePerUser() {
        return maxPurchasePerUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Policy policy = (Policy) o;
        return safetyStock == policy.safetyStock &&
               reservationTimeout == policy.reservationTimeout &&
               maxPurchasePerUser == policy.maxPurchasePerUser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(safetyStock, reservationTimeout, maxPurchasePerUser);
    }

    @Override
    public String toString() {
        return "Policy{" +
                "safetyStock=" + safetyStock +
                ", reservationTimeout=" + reservationTimeout +
                ", maxPurchasePerUser=" + maxPurchasePerUser +
                '}';
    }
}


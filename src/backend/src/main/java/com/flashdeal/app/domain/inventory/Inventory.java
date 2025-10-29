package com.flashdeal.app.domain.inventory;

import com.flashdeal.app.domain.product.ProductId;
import java.util.Objects;

/**
 * Inventory Aggregate Root
 * 
 * 책임:
 * - 재고 수량 관리
 * - 과판매 방지
 * - 예약/확정/해제
 */
public class Inventory {
    
    private final InventoryId inventoryId;
    private final ProductId productId;  // 참조만
    private Stock stock;
    private Policy policy;

    public Inventory(
            InventoryId inventoryId,
            ProductId productId,
            Stock stock,
            Policy policy) {
        
        validateNotNull(inventoryId, "InventoryId cannot be null");
        validateNotNull(productId, "ProductId cannot be null");
        validateNotNull(stock, "Stock cannot be null");
        validateNotNull(policy, "Policy cannot be null");
        
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.stock = stock;
        this.policy = policy;
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 재고 예약 (주문 시)
     */
    public void reserve(int quantity) {
        this.stock = stock.decrease(quantity);
    }

    /**
     * 예약 확정 (결제 완료 시)
     */
    public void confirm(int quantity) {
        this.stock = stock.confirm(quantity);
    }

    /**
     * 예약 해제 (주문 취소 또는 타임아웃 시)
     */
    public void release(int quantity) {
        this.stock = stock.release(quantity);
    }

    /**
     * 재고 증가 (재입고)
     */
    public void increaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        int newTotal = stock.getTotal() + quantity;
        int newAvailable = stock.getAvailable() + quantity;
        
        this.stock = new Stock(
            newTotal,
            stock.getReserved(),
            newAvailable,
            stock.getSold()
        );
    }

    /**
     * 품절 여부
     */
    public boolean isOutOfStock() {
        return stock.isOutOfStock();
    }

    /**
     * 품절 임박 여부
     */
    public boolean isLowStock() {
        return policy.isLowStock(stock.getAvailable());
    }

    /**
     * 구매 가능 수량 확인
     */
    public boolean isValidPurchaseQuantity(int quantity) {
        return policy.isValidPurchaseQuantity(quantity);
    }

    /**
     * 정책 변경
     */
    public void updatePolicy(Policy newPolicy) {
        validateNotNull(newPolicy, "Policy cannot be null");
        this.policy = newPolicy;
    }

    // Getters
    public InventoryId getInventoryId() {
        return inventoryId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public Stock getStock() {
        return stock;
    }

    public Policy getPolicy() {
        return policy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(inventoryId, inventory.inventoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryId);
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId=" + inventoryId +
                ", productId=" + productId +
                ", stock=" + stock +
                '}';
    }
}


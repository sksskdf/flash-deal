package com.flashdeal.app.domain.inventory;

import static com.flashdeal.app.domain.validator.Validator.validateNotNull;

import com.flashdeal.app.domain.product.ProductId;

/**
 * 재고 도메인 모델
 */
public record Inventory(
        InventoryId inventoryId,
        ProductId productId,
        Stock stock,
        Policy policy) {
    /**
     * 재고 생성 시 유효성 검증
     */
    public Inventory {
        validateNotNull(inventoryId, "InventoryId cannot be null");
        validateNotNull(productId, "ProductId cannot be null");
        validateNotNull(stock, "Stock cannot be null");
        validateNotNull(policy, "Policy cannot be null");
    }

    public Inventory reserve(Quantity quantity) {
        return new Inventory(inventoryId, productId, stock.reserve(quantity), policy);
    }

    public Inventory confirm(Quantity quantity) {
        return new Inventory(inventoryId, productId, stock.confirm(quantity), policy);
    }

    public Inventory release(Quantity quantity) {
        return new Inventory(inventoryId, productId, stock.release(quantity), policy);
    }

    /**
     * 재고 증가
     */
    public Inventory increaseStock(Quantity quantity) {
        Quantity newTotal = new Quantity(stock.total().value() + quantity.value());
        Quantity newAvailable = new Quantity(stock.available().value() + quantity.value());

        Stock newStock = new Stock(
                newTotal,
                stock.reserved(),
                newAvailable,
                stock.sold());

        return new Inventory(inventoryId, productId, newStock, policy);
    }

    public Inventory updatePolicy(Policy newPolicy) {
        validateNotNull(newPolicy, "Policy cannot be null");
        return new Inventory(inventoryId, productId, stock, newPolicy);
    }

    public boolean lowStock() {
        return policy.isLowStock(stock.available());
    }
}
package com.flashdeal.app.domain.inventory;

import com.flashdeal.app.domain.product.ProductId;

public record Inventory(
    InventoryId inventoryId,
    ProductId productId,
    Stock stock,
    Policy policy
) {
    public Inventory {
        validateNotNull(inventoryId, "InventoryId cannot be null");
        validateNotNull(productId, "ProductId cannot be null");
        validateNotNull(stock, "Stock cannot be null");
        validateNotNull(policy, "Policy cannot be null");
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public Inventory reserve(int quantity) {
        return new Inventory(inventoryId, productId, stock.reserve(quantity), policy);
    }

    public Inventory confirm(int quantity) {
        return new Inventory(inventoryId, productId, stock.confirm(quantity), policy);
    }

    public Inventory release(int quantity) {
        return new Inventory(inventoryId, productId, stock.release(quantity), policy);
    }

    public Inventory increaseStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        int newTotal = stock.total() + quantity;
        int newAvailable = stock.available() + quantity;
        
        Stock newStock = new Stock(
            newTotal,
            stock.reserved(),
            newAvailable,
            stock.sold()
        );

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
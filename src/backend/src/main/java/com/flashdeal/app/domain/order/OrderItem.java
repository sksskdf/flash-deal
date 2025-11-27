package com.flashdeal.app.domain.order;

import com.flashdeal.app.domain.product.ProductId;
import java.math.BigDecimal;
import java.util.Objects;

public record OrderItem(
    ProductId productId,
    Snapshot snapshot,
    int quantity,
    OrderItemStatus status
) {
    public OrderItem {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot cannot be null");
        }
        
        validateQuantity(quantity);
    }

    public OrderItem(ProductId productId, Snapshot snapshot, int quantity) {
        this(productId, snapshot, quantity, OrderItemStatus.CONFIRMED);
    }

    private static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public OrderItem cancel() {
        return new OrderItem(productId, snapshot, quantity, OrderItemStatus.CANCELLED);
    }

    public OrderItem updateQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        return new OrderItem(productId, snapshot, newQuantity, status);
    }

    public BigDecimal getSubtotal() {
        return snapshot.price().sale().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}


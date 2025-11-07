package com.flashdeal.app.domain.order;

import com.flashdeal.app.domain.product.ProductId;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {
    
    private final ProductId productId;
    private final Snapshot snapshot;
    private int quantity;
    private OrderItemStatus status;

    public OrderItem(ProductId productId, Snapshot snapshot, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot cannot be null");
        }
        
        validateQuantity(quantity);
        
        this.productId = productId;
        this.snapshot = snapshot;
        this.quantity = quantity;
        this.status = OrderItemStatus.CONFIRMED;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public void cancel() {
        this.status = OrderItemStatus.CANCELLED;
    }

    public void updateQuantity(int newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
    }

    public BigDecimal getSubtotal() {
        return snapshot.getPrice().getSale().multiply(BigDecimal.valueOf(quantity));
    }

    public ProductId getProductId() {
        return productId;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderItemStatus getStatus() {
        return status;
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


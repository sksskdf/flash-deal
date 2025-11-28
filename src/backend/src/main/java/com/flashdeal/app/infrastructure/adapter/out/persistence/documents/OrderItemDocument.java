package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import com.flashdeal.app.domain.inventory.Quantity;
import com.flashdeal.app.domain.order.OrderItemStatus;
import java.math.BigDecimal;

public class OrderItemDocument {
    private String productId;
    private String dealType;
    private OrderItemSnapshotDocument snapshot;
    private Quantity quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private OrderItemStatus status;
    private TrackingDocument tracking;

    public OrderItemDocument(String productId, String dealType, OrderItemSnapshotDocument snapshot, Quantity quantity, BigDecimal unitPrice, BigDecimal subtotal, OrderItemStatus status, TrackingDocument tracking) {
        this.productId = productId;
        this.dealType = dealType;
        this.snapshot = snapshot;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
        this.status = status;
        this.tracking = tracking;
    }

    public String getProductId() {
        return productId;
    }

    public String getDealType() {
        return dealType;
    }

    public OrderItemSnapshotDocument getSnapshot() {
        return snapshot;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public TrackingDocument getTracking() {
        return tracking;
    }
}
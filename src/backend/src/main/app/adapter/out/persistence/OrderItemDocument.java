package com.flashdeal.app.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Order Item MongoDB Document
 */
public class OrderItemDocument {
    
    private String productId;
    private String dealType;
    private OrderItemSnapshotDocument snapshot;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String status;
    private TrackingInfoDocument tracking;

    public OrderItemDocument() {}

    public OrderItemDocument(String productId, String dealType, OrderItemSnapshotDocument snapshot,
                           int quantity, BigDecimal unitPrice, BigDecimal subtotal, String status,
                           TrackingInfoDocument tracking) {
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

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public OrderItemSnapshotDocument getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(OrderItemSnapshotDocument snapshot) {
        this.snapshot = snapshot;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TrackingInfoDocument getTracking() {
        return tracking;
    }

    public void setTracking(TrackingInfoDocument tracking) {
        this.tracking = tracking;
    }
}






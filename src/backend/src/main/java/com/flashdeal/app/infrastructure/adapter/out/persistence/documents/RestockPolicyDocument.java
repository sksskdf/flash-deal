package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class RestockPolicyDocument {
    private boolean enabled;
    private int threshold;
    private int quantity;
    private String supplier;

    public RestockPolicyDocument(boolean enabled, int threshold, int quantity, String supplier) {
        this.enabled = enabled;
        this.threshold = threshold;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }
}
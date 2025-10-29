package com.flashdeal.app.infrastructure.adapter.out.persistence;

/**
 * Restock Policy MongoDB Document
 */
public class RestockPolicyDocument {
    
    private boolean enabled;
    private int threshold;
    private int quantity;
    private String supplier;

    public RestockPolicyDocument() {}

    public RestockPolicyDocument(boolean enabled, int threshold, int quantity, String supplier) {
        this.enabled = enabled;
        this.threshold = threshold;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
}






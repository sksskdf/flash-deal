package com.flashdeal.app.infrastructure.adapter.out.persistence;

/**
 * Card MongoDB Document
 */
public class CardDocument {
    
    private String last4;
    private String brand;
    private int expiryMonth;
    private int expiryYear;

    public CardDocument() {}

    public CardDocument(String last4, String brand, int expiryMonth, int expiryYear) {
        this.last4 = last4;
        this.brand = brand;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }
}






package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class CardDocument {
    private String last4;
    private String brand;
    private Integer expiryMonth;
    private Integer expiryYear;

    public CardDocument(String last4, String brand, Integer expiryMonth, Integer expiryYear) {
        this.last4 = last4;
        this.brand = brand;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }

    public String getLast4() {
        return last4;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }
}
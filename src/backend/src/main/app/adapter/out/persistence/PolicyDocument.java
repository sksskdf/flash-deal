package com.flashdeal.app.adapter.out.persistence;

/**
 * Policy MongoDB Document
 */
public class PolicyDocument {
    
    private int safetyStock;
    private RestockPolicyDocument restock;
    private int reservationTimeout;
    private int maxPurchasePerUser;

    public PolicyDocument() {}

    public PolicyDocument(int safetyStock, RestockPolicyDocument restock, int reservationTimeout, int maxPurchasePerUser) {
        this.safetyStock = safetyStock;
        this.restock = restock;
        this.reservationTimeout = reservationTimeout;
        this.maxPurchasePerUser = maxPurchasePerUser;
    }

    public int getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(int safetyStock) {
        this.safetyStock = safetyStock;
    }

    public RestockPolicyDocument getRestock() {
        return restock;
    }

    public void setRestock(RestockPolicyDocument restock) {
        this.restock = restock;
    }

    public int getReservationTimeout() {
        return reservationTimeout;
    }

    public void setReservationTimeout(int reservationTimeout) {
        this.reservationTimeout = reservationTimeout;
    }

    public int getMaxPurchasePerUser() {
        return maxPurchasePerUser;
    }

    public void setMaxPurchasePerUser(int maxPurchasePerUser) {
        this.maxPurchasePerUser = maxPurchasePerUser;
    }
}






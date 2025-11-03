package com.flashdeal.app.infrastructure.adapter.out.persistence;

public class PolicyDocument {
    private int safetyStock;
    private RestockPolicyDocument restock;
    private int reservationTimeout;
    private int maxPurchasePerUser;

    public PolicyDocument(int safetyStock, RestockPolicyDocument restock, int reservationTimeout, int maxPurchasePerUser) {
        this.safetyStock = safetyStock;
        this.restock = restock;
        this.reservationTimeout = reservationTimeout;
        this.maxPurchasePerUser = maxPurchasePerUser;
    }

    public int getSafetyStock() {
        return safetyStock;
    }

    public RestockPolicyDocument getRestock() {
        return restock;
    }

    public int getReservationTimeout() {
        return reservationTimeout;
    }

    public int getMaxPurchasePerUser() {
        return maxPurchasePerUser;
    }
}
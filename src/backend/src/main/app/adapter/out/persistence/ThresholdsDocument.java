package com.flashdeal.app.adapter.out.persistence;

/**
 * Thresholds MongoDB Document
 */
public class ThresholdsDocument {
    
    private double high;
    private double mid;
    private double low;

    public ThresholdsDocument() {}

    public ThresholdsDocument(double high, double mid, double low) {
        this.high = high;
        this.mid = mid;
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }
}






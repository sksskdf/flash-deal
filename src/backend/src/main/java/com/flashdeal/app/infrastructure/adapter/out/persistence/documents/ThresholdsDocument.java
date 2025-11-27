package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class ThresholdsDocument {
    private double high;
    private double mid;
    private double low;

    public ThresholdsDocument(double high, double mid, double low) {
        this.high = high;
        this.mid = mid;
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public double getMid() {
        return mid;
    }

    public double getLow() {
        return low;
    }
}
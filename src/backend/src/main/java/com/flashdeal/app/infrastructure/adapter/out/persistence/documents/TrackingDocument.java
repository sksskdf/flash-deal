package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;

public class TrackingDocument {
    private String carrier;
    private String trackingNumber;
    private Instant shippedAt;
    private Instant estimatedDelivery;

    public TrackingDocument(String carrier, String trackingNumber, Instant shippedAt, Instant estimatedDelivery) {
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.shippedAt = shippedAt;
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Instant getShippedAt() {
        return shippedAt;
    }

    public Instant getEstimatedDelivery() {
        return estimatedDelivery;
    }
}

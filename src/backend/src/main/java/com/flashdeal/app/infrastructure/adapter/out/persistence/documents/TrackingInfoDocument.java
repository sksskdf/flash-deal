package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;
import java.util.List;

/**
 * Tracking Info MongoDB Document
 */
public class TrackingInfoDocument {
    
    private String carrier;
    private String trackingNumber;
    private String status;
    private List<TrackingEventDocument> events;
    private Instant estimatedDelivery;
    private Instant deliveredAt;

    public TrackingInfoDocument() {}

    public TrackingInfoDocument(String carrier, String trackingNumber, String status,
                               List<TrackingEventDocument> events, Instant estimatedDelivery,
                               Instant deliveredAt) {
        this.carrier = carrier;
        this.trackingNumber = trackingNumber;
        this.status = status;
        this.events = events;
        this.estimatedDelivery = estimatedDelivery;
        this.deliveredAt = deliveredAt;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<TrackingEventDocument> getEvents() {
        return events;
    }

    public void setEvents(List<TrackingEventDocument> events) {
        this.events = events;
    }

    public Instant getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(Instant estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}






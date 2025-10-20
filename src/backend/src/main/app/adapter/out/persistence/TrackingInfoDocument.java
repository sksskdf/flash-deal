package com.flashdeal.app.adapter.out.persistence;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Tracking Info MongoDB Document
 */
public class TrackingInfoDocument {
    
    private String carrier;
    private String trackingNumber;
    private String status;
    private List<TrackingEventDocument> events;
    private ZonedDateTime estimatedDelivery;
    private ZonedDateTime deliveredAt;

    public TrackingInfoDocument() {}

    public TrackingInfoDocument(String carrier, String trackingNumber, String status,
                               List<TrackingEventDocument> events, ZonedDateTime estimatedDelivery,
                               ZonedDateTime deliveredAt) {
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

    public ZonedDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(ZonedDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public ZonedDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(ZonedDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
}






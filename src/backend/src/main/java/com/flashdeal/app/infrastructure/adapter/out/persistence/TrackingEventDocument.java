package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.time.Instant;

/**
 * Tracking Event MongoDB Document
 */
public class TrackingEventDocument {
    
    private String status;
    private String description;
    private Instant timestamp;
    private String location;

    public TrackingEventDocument() {}

    public TrackingEventDocument(String status, String description, Instant timestamp, String location) {
        this.status = status;
        this.description = description;
        this.timestamp = timestamp;
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}






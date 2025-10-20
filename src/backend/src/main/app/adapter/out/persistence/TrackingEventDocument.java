package com.flashdeal.app.adapter.out.persistence;

import java.time.ZonedDateTime;

/**
 * Tracking Event MongoDB Document
 */
public class TrackingEventDocument {
    
    private String status;
    private String description;
    private ZonedDateTime timestamp;
    private String location;

    public TrackingEventDocument() {}

    public TrackingEventDocument(String status, String description, ZonedDateTime timestamp, String location) {
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

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}






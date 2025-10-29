package com.flashdeal.app.infrastructure.adapter.out.persistence;

/**
 * Coordinates MongoDB Document
 */
public class CoordinatesDocument {
    
    private double lat;
    private double lng;

    public CoordinatesDocument() {}

    public CoordinatesDocument(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}






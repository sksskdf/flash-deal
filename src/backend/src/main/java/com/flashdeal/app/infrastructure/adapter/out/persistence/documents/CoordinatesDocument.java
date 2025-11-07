package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class CoordinatesDocument {
    private Double lat;
    private Double lng;

    public CoordinatesDocument(Double lat, Double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }
}
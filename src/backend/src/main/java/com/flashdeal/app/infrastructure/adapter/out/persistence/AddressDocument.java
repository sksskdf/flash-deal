package com.flashdeal.app.infrastructure.adapter.out.persistence;

public class AddressDocument {
    private String type;
    private String street;
    private String street2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private CoordinatesDocument coordinates;

    public AddressDocument(String type, String street, String street2, String city, String state, String zipCode, String country, CoordinatesDocument coordinates) {
        this.type = type;
        this.street = street;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public String getStreet() {
        return street;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public CoordinatesDocument getCoordinates() {
        return coordinates;
    }
}
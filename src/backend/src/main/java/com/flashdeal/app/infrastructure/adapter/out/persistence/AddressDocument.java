package com.flashdeal.app.infrastructure.adapter.out.persistence;

/**
 * Address MongoDB Document
 */
public class AddressDocument {
    
    private String type;
    private String street;
    private String street2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private CoordinatesDocument coordinates;

    public AddressDocument() {}

    public AddressDocument(String type, String street, String street2, String city,
                         String state, String zipCode, String country, CoordinatesDocument coordinates) {
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

    public void setType(String type) {
        this.type = type;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CoordinatesDocument getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(CoordinatesDocument coordinates) {
        this.coordinates = coordinates;
    }
}






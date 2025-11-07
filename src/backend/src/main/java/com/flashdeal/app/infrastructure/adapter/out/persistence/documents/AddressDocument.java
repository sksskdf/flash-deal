package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class AddressDocument {
    private String street;
    private String city;
    private String zipCode;
    private String country;

    public AddressDocument(String street, String city, String zipCode, String country) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }
}
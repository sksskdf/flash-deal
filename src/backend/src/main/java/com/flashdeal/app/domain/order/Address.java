package com.flashdeal.app.domain.order;

public record Address(
        String street,
        String city,
        String zipCode,
        String country) {
    public Address {
        validateNotEmpty(street, "Street");
        validateNotEmpty(city, "City");
        validateNotEmpty(zipCode, "ZipCode");
        validateNotEmpty(country, "Country");
        
        if (country.length() != 2) {
            throw new IllegalArgumentException("Country code must be 2 characters");
        }
    }

    private static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
}


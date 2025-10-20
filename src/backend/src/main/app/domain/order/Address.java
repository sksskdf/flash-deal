package com.flashdeal.app.domain.order;

import java.util.Objects;

/**
 * 주소 Value Object
 */
public final class Address {
    
    private final String street;
    private final String city;
    private final String zipCode;
    private final String country;

    public Address(String street, String city, String zipCode, String country) {
        validateNotEmpty(street, "Street");
        validateNotEmpty(city, "City");
        validateNotEmpty(zipCode, "ZipCode");
        validateNotEmpty(country, "Country");
        
        if (country.length() != 2) {
            throw new IllegalArgumentException("Country code must be 2 characters");
        }
        
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
    }

    private void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
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

    /**
     * 주소 타입 (임시 구현)
     */
    public String getType() {
        return "HOME";
    }

    /**
     * 상세 주소 (임시 구현)
     */
    public String getStreet2() {
        return "";
    }

    /**
     * 주/도 (임시 구현)
     */
    public String getState() {
        return "Seoul";
    }

    /**
     * 좌표 정보 (임시 구현)
     */
    public Coordinates getCoordinates() {
        return new Coordinates(37.5665, 126.9780);
    }

    /**
     * 좌표 내부 클래스
     */
    public static class Coordinates {
        private final double lat;
        private final double lng;

        public Coordinates(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
               Objects.equals(city, address.city) &&
               Objects.equals(zipCode, address.zipCode) &&
               Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zipCode, country);
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}


package com.flashdeal.app.domain.order;

import java.util.Objects;

/**
 * 배송 정보 Value Object
 */
public final class Shipping {
    
    private final String method;
    private final Recipient recipient;
    private final Address address;
    private final String instructions;

    public Shipping(String method, Recipient recipient, Address address, String instructions) {
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Method cannot be null or empty");
        }
        
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient cannot be null");
        }
        
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        
        this.method = method;
        this.recipient = recipient;
        this.address = address;
        this.instructions = instructions;
    }

    public String getMethod() {
        return method;
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public Address getAddress() {
        return address;
    }

    public String getInstructions() {
        return instructions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipping shipping = (Shipping) o;
        return Objects.equals(method, shipping.method) &&
               Objects.equals(recipient, shipping.recipient) &&
               Objects.equals(address, shipping.address) &&
               Objects.equals(instructions, shipping.instructions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, recipient, address, instructions);
    }

    @Override
    public String toString() {
        return "Shipping{" +
                "method='" + method + '\'' +
                ", recipient=" + recipient +
                ", address=" + address +
                '}';
    }
}


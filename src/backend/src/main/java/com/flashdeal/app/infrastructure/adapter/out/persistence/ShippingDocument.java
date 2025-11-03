package com.flashdeal.app.infrastructure.adapter.out.persistence;

public class ShippingDocument {
    private String method;
    private RecipientDocument recipient;
    private AddressDocument address;
    private String instructions;
    private String preferredTime;

    public ShippingDocument(String method, RecipientDocument recipient, AddressDocument address, String instructions, String preferredTime) {
        this.method = method;
        this.recipient = recipient;
        this.address = address;
        this.instructions = instructions;
        this.preferredTime = preferredTime;
    }

    public String getMethod() {
        return method;
    }

    public RecipientDocument getRecipient() {
        return recipient;
    }

    public AddressDocument getAddress() {
        return address;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getPreferredTime() {
        return preferredTime;
    }
}
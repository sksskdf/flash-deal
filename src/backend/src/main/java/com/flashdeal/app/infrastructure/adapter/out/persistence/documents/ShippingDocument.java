package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class ShippingDocument {
    private String method;
    private RecipientDocument recipient;
    private AddressDocument address;
    private String instructions;

    public ShippingDocument(String method, RecipientDocument recipient, AddressDocument address, String instructions) {
        this.method = method;
        this.recipient = recipient;
        this.address = address;
        this.instructions = instructions;
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
}
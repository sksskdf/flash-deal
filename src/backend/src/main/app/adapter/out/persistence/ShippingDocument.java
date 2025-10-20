package com.flashdeal.app.adapter.out.persistence;

/**
 * Shipping MongoDB Document
 */
public class ShippingDocument {
    
    private String method;
    private RecipientDocument recipient;
    private AddressDocument address;
    private String instructions;
    private String preferredTime;

    public ShippingDocument() {}

    public ShippingDocument(String method, RecipientDocument recipient, AddressDocument address,
                          String instructions, String preferredTime) {
        this.method = method;
        this.recipient = recipient;
        this.address = address;
        this.instructions = instructions;
        this.preferredTime = preferredTime;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RecipientDocument getRecipient() {
        return recipient;
    }

    public void setRecipient(RecipientDocument recipient) {
        this.recipient = recipient;
    }

    public AddressDocument getAddress() {
        return address;
    }

    public void setAddress(AddressDocument address) {
        this.address = address;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }
}






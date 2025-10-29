package com.flashdeal.app.infrastructure.adapter.out.persistence;

/**
 * Recipient MongoDB Document
 */
public class RecipientDocument {
    
    private String name;
    private String phone;

    public RecipientDocument() {}

    public RecipientDocument(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}






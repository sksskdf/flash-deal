package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class RecipientDocument {
    private String name;
    private String phone;

    public RecipientDocument(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
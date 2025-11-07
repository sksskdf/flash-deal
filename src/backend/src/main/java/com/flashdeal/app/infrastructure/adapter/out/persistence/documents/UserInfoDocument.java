package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class UserInfoDocument {
    private String id;
    private String email;
    private String name;
    private String phone;

    public UserInfoDocument(String id, String email, String name, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
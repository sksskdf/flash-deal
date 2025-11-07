package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.util.Map;

public class OrderItemSnapshotDocument {
    private String title;
    private String subtitle;
    private String image;
    private String category;
    private PriceDocument price;
    private Map<String, Object> specs;
    private Map<String, Object> selectedOptions;

    public OrderItemSnapshotDocument(String title, String subtitle, String image, String category, PriceDocument price, Map<String, Object> specs, Map<String, Object> selectedOptions) {
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
        this.category = category;
        this.price = price;
        this.specs = specs;
        this.selectedOptions = selectedOptions;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public PriceDocument getPrice() {
        return price;
    }

    public Map<String, Object> getSpecs() {
        return specs;
    }

    public Map<String, Object> getSelectedOptions() {
        return selectedOptions;
    }
}
package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Order Item Snapshot MongoDB Document
 */
public class OrderItemSnapshotDocument {
    
    private String title;
    private String subtitle;
    private String image;
    private String category;
    private PriceDocument price;
    private Map<String, Object> specs;
    private Map<String, Object> selectedOptions;

    public OrderItemSnapshotDocument() {}

    public OrderItemSnapshotDocument(String title, String subtitle, String image, String category,
                                   PriceDocument price, Map<String, Object> specs,
                                   Map<String, Object> selectedOptions) {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public PriceDocument getPrice() {
        return price;
    }

    public void setPrice(PriceDocument price) {
        this.price = price;
    }

    public Map<String, Object> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, Object> specs) {
        this.specs = specs;
    }

    public Map<String, Object> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(Map<String, Object> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}






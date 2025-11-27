package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import com.flashdeal.app.domain.product.DealStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Product MongoDB Document
 * 
 * .doc/data/1.model-structure.md의 Product 스키마를 기반으로 구현
 */
@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    @Field("dealType")
    private String dealType;

    @Field("title")
    private String title;

    @Field("subtitle")
    private String subtitle;

    @Field("description")
    private String description;

    @Field("category")
    private String category;

    @Field("images")
    private List<String> images;

    @Field("price")
    private PriceDocument price;

    @Field("schedule")
    private ScheduleDocument schedule;

    @Field("status")
    private DealStatus status;

    @Field("specs")
    private Map<String, Object> specs;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Field("createdAt")
    private Instant createdAt;

    @Field("updatedAt")
    private Instant updatedAt;

    // Constructors
    public ProductDocument() {}

    public ProductDocument(String id, String dealType, String title, String subtitle, 
                          String description, String category, List<String> images,
                          PriceDocument price, ScheduleDocument schedule, DealStatus status,
                          Map<String, Object> specs, Map<String, Object> metadata,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.dealType = dealType;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.category = category;
        this.images = images;
        this.price = price;
        this.schedule = schedule;
        this.status = status;
        this.specs = specs;
        this.metadata = metadata;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public PriceDocument getPrice() {
        return price;
    }

    public void setPrice(PriceDocument price) {
        this.price = price;
    }

    public ScheduleDocument getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleDocument schedule) {
        this.schedule = schedule;
    }

    public DealStatus getStatus() {
        return status;
    }

    public void setStatus(DealStatus status) {
        this.status = status;
    }

    public Map<String, Object> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, Object> specs) {
        this.specs = specs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}






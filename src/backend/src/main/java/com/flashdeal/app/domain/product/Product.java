package com.flashdeal.app.domain.product;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Product {
    
    private final ProductId productId;
    private String title;
    private String description;
    private String category;
    private Price price;
    private Schedule schedule;
    private Specs specs;
    private DealStatus status;

    public Product(
            ProductId productId,
            String title,
            String description,
            Price price,
            Schedule schedule,
            Specs specs) {
        this(productId, title, description, null, price, schedule, specs);
    }

    public Product(
            ProductId productId,
            String title,
            String description,
            String category,
            Price price,
            Schedule schedule,
            Specs specs) {
        
        validateNotNull(productId, "ProductId cannot be null");
        validateNotNull(title, "Title cannot be null");
        validateNotNull(price, "Price cannot be null");
        validateNotNull(schedule, "Schedule cannot be null");
        validateNotNull(specs, "Specs cannot be null");
        
        if (title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.schedule = schedule;
        this.specs = specs;
        this.status = DealStatus.UPCOMING;
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public DealStatus calculateStatus(ZonedDateTime now) {
        if (status == DealStatus.SOLDOUT || status == DealStatus.ENDED) {
            return status;
        }

        if (!schedule.hasStarted(now)) {
            return DealStatus.UPCOMING;
        } else if (schedule.isActive(now)) {
            return DealStatus.ACTIVE;
        } else {
            return DealStatus.ENDED;
        }
    }

    public void transitionTo(DealStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
        this.status = newStatus;
    }

    public void updatePrice(Price newPrice) {
        validateNotNull(newPrice, "Price cannot be null");
        this.price = newPrice;
    }

    public void updateSchedule(Schedule newSchedule) {
        validateNotNull(newSchedule, "Schedule cannot be null");
        this.schedule = newSchedule;
    }

    public void updateSpecs(Specs newSpecs) {
        validateNotNull(newSpecs, "Specs cannot be null");
        this.specs = newSpecs;
    }

    public void updateTitle(String newTitle) {
        validateNotNull(newTitle, "Title cannot be null");
        if (newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = newTitle;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updateCategory(String newCategory) {
        this.category = newCategory;
    }

    public void updateStatus(DealStatus newStatus) {
        this.status = newStatus;
    }

    public ProductId getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Price getPrice() {
        return price;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public Specs getSpecs() {
        return specs;
    }

    public DealStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", price=" + price +
                ", schedule=" + schedule +
                ", specs=" + specs +
                ", description=" + description +
                '}';
    }
}
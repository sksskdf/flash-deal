package com.flashdeal.app.domain.product;

import java.time.ZonedDateTime;

public record Product (
    ProductId productId,
    String title,
    String description,
    String category,
    Price price,
    Schedule schedule,
    Specs specs,
    DealStatus status
) {
    public Product {
        validateNotNull(productId, "ProductId cannot be null");
        validateNotNull(title, "Title cannot be null");
        validateNotNull(price, "Price cannot be null");
        validateNotNull(schedule, "Schedule cannot be null");
        validateNotNull(specs, "Specs cannot be null");
        validateNotNull(status, "Status cannot be null");
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

    public Product transitionTo(DealStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
        return new Product(productId, title, description, category, price, schedule, specs, newStatus);
    }

    public Product updatePrice(Price newPrice) {
        return new Product(productId, title, description, category, newPrice, schedule, specs, status);
    }

    public Product updateSchedule(Schedule newSchedule) {
        return new Product(productId, title, description, category, price, newSchedule, specs, status);
    }

    public Product updateSpecs(Specs newSpecs) {
        return new Product(productId, title, description, category, price, schedule, newSpecs, status);
    }

    public Product updateTitle(String newTitle) {
        return new Product(productId, newTitle, description, category, price, schedule, specs, status);
    }

    public Product updateDescription(String newDescription) {
        return new Product(productId, title, newDescription, category, price, schedule, specs, status);
    }

    public Product updateCategory(String newCategory) {
        return new Product(productId, title, description, newCategory, price, schedule, specs, status);
    }

    public Product updateStatus(DealStatus newStatus) {
        return new Product(productId, title, description, category, price, schedule, specs, newStatus);
    }
}
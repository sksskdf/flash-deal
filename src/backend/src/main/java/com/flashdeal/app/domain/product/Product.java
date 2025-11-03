package com.flashdeal.app.domain.product;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Product Aggregate Root
 * 
 * 책임:
 * - 상품 정보 관리
 * - 딜 상태 전이 (upcoming → active → ended/soldout)
 * - 가격 계산
 */
public class Product {
    
    private final ProductId productId;
    private String title;
    private String description;
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

    /**
     * 현재 시각 기준으로 딜 상태를 계산
     */
    public DealStatus calculateStatus(ZonedDateTime now) {
        // SOLDOUT이나 ENDED는 유지
        if (status == DealStatus.SOLDOUT || status == DealStatus.ENDED) {
            return status;
        }
        
        // 일정 기반으로 상태 계산
        if (!schedule.hasStarted(now)) {
            return DealStatus.UPCOMING;
        } else if (schedule.isActive(now)) {
            return DealStatus.ACTIVE;
        } else {
            return DealStatus.ENDED;
        }
    }

    /**
     * 딜 상태를 전이
     */
    public void transitionTo(DealStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
        this.status = newStatus;
    }

    /**
     * 가격 변경
     */
    public void updatePrice(Price newPrice) {
        validateNotNull(newPrice, "Price cannot be null");
        this.price = newPrice;
    }

    /**
     * 일정 변경
     */
    public void updateSchedule(Schedule newSchedule) {
        validateNotNull(newSchedule, "Schedule cannot be null");
        this.schedule = newSchedule;
    }

    /**
     * 스펙 변경
     */
    public void updateSpecs(Specs newSpecs) {
        validateNotNull(newSpecs, "Specs cannot be null");
        this.specs = newSpecs;
    }

    /**
     * 제목 변경
     */
    public void updateTitle(String newTitle) {
        validateNotNull(newTitle, "Title cannot be null");
        if (newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = newTitle;
    }

    /**
     * 설명 변경
     */
    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    /**
     * 상태 변경
     */
    public void updateStatus(DealStatus newStatus) {
        this.status = newStatus;
    }

    // Getters
    public ProductId getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
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
                ", status=" + status +
                '}';
    }
}


package com.flashdeal.app.domain.order;

import com.flashdeal.app.domain.product.Price;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 주문 스냅샷 Value Object
 * 
 * 주문 시점의 상품 정보를 보존 (Product 변경돼도 유지)
 */
public final class Snapshot {
    
    private final String title;
    private final String image;
    private final Price price;
    private final Map<String, Object> selectedOptions;

    public Snapshot(String title, String image, Price price, Map<String, Object> selectedOptions) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        
        if (selectedOptions == null) {
            throw new IllegalArgumentException("Selected options cannot be null");
        }
        
        this.title = title;
        this.image = image;
        this.price = price;
        this.selectedOptions = new HashMap<>(selectedOptions);
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public Price getPrice() {
        return price;
    }

    public Map<String, Object> getSelectedOptions() {
        return Collections.unmodifiableMap(selectedOptions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Snapshot snapshot = (Snapshot) o;
        return Objects.equals(title, snapshot.title) &&
               Objects.equals(image, snapshot.image) &&
               Objects.equals(price, snapshot.price) &&
               Objects.equals(selectedOptions, snapshot.selectedOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, image, price, selectedOptions);
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "title='" + title + '\'' +
                ", price=" + price +
                '}';
    }
}


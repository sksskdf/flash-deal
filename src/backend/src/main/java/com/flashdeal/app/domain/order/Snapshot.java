package com.flashdeal.app.domain.order;

import com.flashdeal.app.domain.product.Price;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 주문 스냅샷 Value Object
 * 
 * 주문 시점의 상품 정보를 보존 (Product 변경돼도 유지)
 */
public record Snapshot(
    String title,
    String image,
    Price price,
    Map<String, Object> selectedOptions
) {
    public Snapshot {
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
        
        // Make a defensive copy
        selectedOptions = new HashMap<>(selectedOptions);
    }

    public Map<String, Object> getSelectedOptions() {
        return Collections.unmodifiableMap(selectedOptions);
    }
}


package com.flashdeal.app.domain.order;

import com.flashdeal.app.domain.product.Price;

import static com.flashdeal.app.domain.validator.Validator.validateNotEmpty;
import static com.flashdeal.app.domain.validator.Validator.validateNotNull;

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
        validateNotEmpty(title, "Title cannot be null or empty");
        validateNotNull(image, "Image cannot be null");
        validateNotNull(price, "Price cannot be null");
        validateNotNull(selectedOptions, "Selected options cannot be null");

        selectedOptions = new HashMap<>(selectedOptions);
    }

    public Map<String, Object> getSelectedOptions() {
        return Collections.unmodifiableMap(selectedOptions);
    }
}


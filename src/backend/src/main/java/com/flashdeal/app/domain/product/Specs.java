package com.flashdeal.app.domain.product;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 상품 스펙 Value Object (동적 필드 지원)
 */
public record Specs(Map<String, Object> fields) {
    public Specs {
        if (fields == null) {
            throw new IllegalArgumentException("Fields cannot be null");
        }
        // 불변성을 위해 복사본 생성
        fields = Collections.unmodifiableMap(new HashMap<>(fields));
    }

    /**
     * 필드 값 조회
     */
    public Object get(String key) {
        return fields.get(key);
    }

    /**
     * 필드 존재 여부 확인
     */
    public boolean has(String key) {
        return fields.containsKey(key);
    }

    /**
     * 모든 필드 반환 (불변)
     */
    public Map<String, Object> getFields() {
        return fields;
    }
}


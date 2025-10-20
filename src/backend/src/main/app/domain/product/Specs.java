package com.flashdeal.app.domain.product;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 상품 스펙 Value Object (동적 필드 지원)
 */
public final class Specs {
    
    private final Map<String, Object> fields;

    public Specs(Map<String, Object> fields) {
        if (fields == null) {
            throw new IllegalArgumentException("Fields cannot be null");
        }
        // 불변성을 위해 복사본 생성
        this.fields = new HashMap<>(fields);
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
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specs specs = (Specs) o;
        return Objects.equals(fields, specs.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }

    @Override
    public String toString() {
        return "Specs{" +
                "fields=" + fields +
                '}';
    }
}


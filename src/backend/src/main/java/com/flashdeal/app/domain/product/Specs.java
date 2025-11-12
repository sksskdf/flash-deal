package com.flashdeal.app.domain.product;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record Specs(Map<String, Object> fields) {
    public Specs {
        if (fields == null) {
            throw new IllegalArgumentException("Fields cannot be null");
        }
        fields = Collections.unmodifiableMap(new HashMap<>(fields));
    }

    public Object get(String key) {
        return fields.get(key);
    }

    public boolean has(String key) {
        return fields.containsKey(key);
    }

    public Map<String, Object> getFields() {
        return fields;
    }
}


package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;
import java.util.Map;

public class KafkaEventDocument {
    private String eventType;
    private String topic;
    private Integer partition;
    private Long offset;
    private Instant publishedAt;
    private Map<String, Object> payload;

    public KafkaEventDocument(String eventType, String topic, Integer partition, Long offset, Instant publishedAt, Map<String, Object> payload) {
        this.eventType = eventType;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.publishedAt = publishedAt;
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTopic() {
        return topic;
    }

    public Integer getPartition() {
        return partition;
    }

    public Long getOffset() {
        return offset;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
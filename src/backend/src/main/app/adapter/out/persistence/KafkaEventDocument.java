package com.flashdeal.app.adapter.out.persistence;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Kafka Event MongoDB Document
 */
public class KafkaEventDocument {
    
    private String eventId;
    private String eventType;
    private String topic;
    private int partition;
    private long offset;
    private ZonedDateTime publishedAt;
    private Map<String, Object> payload;

    public KafkaEventDocument() {}

    public KafkaEventDocument(String eventId, String eventType, String topic, int partition,
                            long offset, ZonedDateTime publishedAt, Map<String, Object> payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.publishedAt = publishedAt;
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public ZonedDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(ZonedDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}






package com.flashdeal.app.infrastructure.adapter.out.messaging;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 도메인 이벤트 기본 클래스
 * 
 * .doc/data/3.kafka-events.md의 이벤트 스키마를 기반으로 구현
 */
public abstract class DomainEvent {
    
    @JsonProperty("eventId")
    private final String eventId;
    
    @JsonProperty("eventType")
    private final String eventType;
    
    @JsonProperty("eventVersion")
    private final String eventVersion;
    
    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime timestamp;
    
    @JsonProperty("aggregateId")
    private final String aggregateId;
    
    @JsonProperty("correlationId")
    private final String correlationId;
    
    @JsonProperty("causationId")
    private final String causationId;
    
    @JsonProperty("metadata")
    private final Map<String, Object> metadata;
    
    protected DomainEvent(String eventType, String eventVersion, String aggregateId, 
                         String correlationId, String causationId, Map<String, Object> metadata) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.timestamp = ZonedDateTime.now();
        this.aggregateId = aggregateId;
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.metadata = metadata;
    }
    
    // Getters
    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getEventVersion() { return eventVersion; }
    public ZonedDateTime getTimestamp() { return timestamp; }
    public String getAggregateId() { return aggregateId; }
    public String getCorrelationId() { return correlationId; }
    public String getCausationId() { return causationId; }
    public Map<String, Object> getMetadata() { return metadata; }
}

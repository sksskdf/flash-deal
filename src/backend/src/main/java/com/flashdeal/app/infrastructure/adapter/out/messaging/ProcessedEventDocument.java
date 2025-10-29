package com.flashdeal.app.infrastructure.adapter.out.messaging;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 처리된 이벤트 문서 (멱등성 처리용)
 */
@Document(collection = "processed_events")
public class ProcessedEventDocument {
    
    @Id
    private String id;
    
    private String eventId;
    private String eventType;
    private String aggregateId;
    private Instant processedAt;
    private String processorId;
    private String status; // PROCESSED, FAILED, RETRYING
    private String errorMessage;
    private int retryCount;
    
    public ProcessedEventDocument() {}
    
    public ProcessedEventDocument(String eventId, String eventType, String aggregateId, 
                                 String processorId, String status) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.processorId = processorId;
        this.status = status;
        this.processedAt = Instant.now();
        this.retryCount = 0;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    
    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
    
    public String getProcessorId() { return processorId; }
    public void setProcessorId(String processorId) { this.processorId = processorId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
}

package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;
import java.util.Map;

public class StatusChangeDocument {
    private String from;
    private String to;
    private Instant timestamp;
    private String reason;
    private String actor;
    private Map<String, Object> metadata;

    public StatusChangeDocument(String from, String to, Instant timestamp, String reason, String actor, Map<String, Object> metadata) {
        this.from = from;
        this.to = to;
        this.timestamp = timestamp;
        this.reason = reason;
        this.actor = actor;
        this.metadata = metadata;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getReason() {
        return reason;
    }

    public String getActor() {
        return actor;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
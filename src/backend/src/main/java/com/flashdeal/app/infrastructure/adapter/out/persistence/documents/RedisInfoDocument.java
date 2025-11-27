package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.time.Instant;

public class RedisInfoDocument {
    private String key;
    private long currentValue;
    private Instant lastSyncedAt;
    private long syncVersion;

    public RedisInfoDocument(String key, long currentValue, Instant lastSyncedAt, long syncVersion) {
        this.key = key;
        this.currentValue = currentValue;
        this.lastSyncedAt = lastSyncedAt;
        this.syncVersion = syncVersion;
    }

    public String getKey() {
        return key;
    }

    public long getCurrentValue() {
        return currentValue;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public long getSyncVersion() {
        return syncVersion;
    }
}
package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.time.Instant;

/**
 * Redis Info MongoDB Document
 */
public class RedisInfoDocument {
    
    private String key;
    private int currentValue;
    private Instant lastSyncedAt;
    private int syncVersion;

    public RedisInfoDocument() {}

    public RedisInfoDocument(String key, int currentValue, Instant lastSyncedAt, int syncVersion) {
        this.key = key;
        this.currentValue = currentValue;
        this.lastSyncedAt = lastSyncedAt;
        this.syncVersion = syncVersion;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public int getSyncVersion() {
        return syncVersion;
    }

    public void setSyncVersion(int syncVersion) {
        this.syncVersion = syncVersion;
    }
}






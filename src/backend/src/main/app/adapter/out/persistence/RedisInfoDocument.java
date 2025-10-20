package com.flashdeal.app.adapter.out.persistence;

import java.time.ZonedDateTime;

/**
 * Redis Info MongoDB Document
 */
public class RedisInfoDocument {
    
    private String key;
    private int currentValue;
    private ZonedDateTime lastSyncedAt;
    private int syncVersion;

    public RedisInfoDocument() {}

    public RedisInfoDocument(String key, int currentValue, ZonedDateTime lastSyncedAt, int syncVersion) {
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

    public ZonedDateTime getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(ZonedDateTime lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public int getSyncVersion() {
        return syncVersion;
    }

    public void setSyncVersion(int syncVersion) {
        this.syncVersion = syncVersion;
    }
}






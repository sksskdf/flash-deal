package com.flashdeal.app.infrastructure.adapter.out.persistence;

import java.time.Instant;

/**
 * Schedule MongoDB Document
 */
public class ScheduleDocument {
    
    private Instant startsAt;
    private Instant endsAt;
    private String timezone;

    public ScheduleDocument() {}

    public ScheduleDocument(Instant startsAt, Instant endsAt, String timezone) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.timezone = timezone;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}






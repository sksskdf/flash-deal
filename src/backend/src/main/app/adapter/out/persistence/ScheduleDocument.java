package com.flashdeal.app.adapter.out.persistence;

import java.time.ZonedDateTime;

/**
 * Schedule MongoDB Document
 */
public class ScheduleDocument {
    
    private ZonedDateTime startsAt;
    private ZonedDateTime endsAt;
    private String timezone;

    public ScheduleDocument() {}

    public ScheduleDocument(ZonedDateTime startsAt, ZonedDateTime endsAt, String timezone) {
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.timezone = timezone;
    }

    public ZonedDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(ZonedDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public ZonedDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(ZonedDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}






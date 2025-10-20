package com.flashdeal.app.domain.product;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * 딜 일정 Value Object
 */
public final class Schedule {
    
    private final ZonedDateTime startsAt;
    private final ZonedDateTime endsAt;
    private final String timezone;

    public Schedule(ZonedDateTime startsAt, ZonedDateTime endsAt, String timezone) {
        validateNotNull(startsAt, "Start time cannot be null");
        validateNotNull(endsAt, "End time cannot be null");
        validateNotNull(timezone, "Timezone cannot be null");
        
        if (startsAt.isAfter(endsAt) || startsAt.isEqual(endsAt)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.timezone = timezone;
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 현재 시각이 일정 내에 있는지 확인
     * startsAt <= now < endsAt
     */
    public boolean isActive(ZonedDateTime now) {
        return (now.isEqual(startsAt) || now.isAfter(startsAt)) && now.isBefore(endsAt);
    }

    /**
     * 딜이 시작되었는지 확인
     * now >= startsAt
     */
    public boolean hasStarted(ZonedDateTime now) {
        return now.isEqual(startsAt) || now.isAfter(startsAt);
    }

    public ZonedDateTime getStartsAt() {
        return startsAt;
    }

    public ZonedDateTime getEndsAt() {
        return endsAt;
    }

    public String getTimezone() {
        return timezone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(startsAt, schedule.startsAt) &&
               Objects.equals(endsAt, schedule.endsAt) &&
               Objects.equals(timezone, schedule.timezone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startsAt, endsAt, timezone);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "startsAt=" + startsAt +
                ", endsAt=" + endsAt +
                ", timezone='" + timezone + '\'' +
                '}';
    }
}


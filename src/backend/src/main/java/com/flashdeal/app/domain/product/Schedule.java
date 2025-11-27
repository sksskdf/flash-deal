package com.flashdeal.app.domain.product;

import java.time.ZonedDateTime;

public record Schedule(
    ZonedDateTime startsAt,
    ZonedDateTime endsAt,
    String timezone
) {
    public Schedule {
        if (startsAt == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endsAt == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (timezone == null) {
            throw new IllegalArgumentException("Timezone cannot be null");
        }
        
        if (startsAt.isAfter(endsAt) || startsAt.isEqual(endsAt)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }

    public boolean isActive(ZonedDateTime now) {
        return (now.isEqual(startsAt) || now.isAfter(startsAt)) && now.isBefore(endsAt);
    }

    public boolean hasStarted(ZonedDateTime now) {
        return now.isEqual(startsAt) || now.isAfter(startsAt);
    }
}
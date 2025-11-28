package com.flashdeal.app.domain.product;

import static com.flashdeal.app.domain.validator.Validator.validateNotNull;

import java.time.ZonedDateTime;

public record Schedule(
        ZonedDateTime startsAt,
        ZonedDateTime endsAt,
        String timezone) {
    public Schedule {
        validateNotNull(startsAt, "Start time cannot be null");
        validateNotNull(endsAt, "End time cannot be null");
        validateNotNull(timezone, "Timezone cannot be null");

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
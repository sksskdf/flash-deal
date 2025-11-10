package com.flashdeal.app.domain.product;

import java.time.ZonedDateTime;

/**
 * 딜 일정 Value Object
 */
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
}
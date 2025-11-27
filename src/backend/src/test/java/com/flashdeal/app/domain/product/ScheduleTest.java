package com.flashdeal.app.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.ZonedDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Schedule Value Object 테스트")
class ScheduleTest {

    @Test
    @DisplayName("유효한 일정으로 Schedule을 생성할 수 있다")
    void createScheduleWithValidValues() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endsAt = startsAt.plusHours(24);
        String timezone = "Asia/Seoul";
        
        // when
        Schedule schedule = new Schedule(startsAt, endsAt, timezone);
        
        // then
        assertNotNull(schedule);
        assertEquals(startsAt, schedule.startsAt());
        assertEquals(endsAt, schedule.endsAt());
        assertEquals(timezone, schedule.timezone());
    }

    @Test
    @DisplayName("종료 시각이 시작 시각보다 이전이면 예외가 발생한다")
    void throwsExceptionWhenEndsAtIsBeforeStartsAt() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endsAt = startsAt.minusHours(1);
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Schedule(startsAt, endsAt, "Asia/Seoul"));
    }

    @Test
    @DisplayName("종료 시각과 시작 시각이 같으면 예외가 발생한다")
    void throwsExceptionWhenEndsAtEqualsStartsAt() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endsAt = startsAt;
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Schedule(startsAt, endsAt, "Asia/Seoul"));
    }

    @Test
    @DisplayName("현재 시각이 일정 내에 있으면 isActive는 true를 반환한다")
    void isActiveReturnsTrueWhenNowIsInSchedule() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(1);
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        boolean active = schedule.isActive(now);
        
        // then
        assertTrue(active);
    }

    @Test
    @DisplayName("현재 시각이 시작 전이면 isActive는 false를 반환한다")
    void isActiveReturnsFalseWhenNowIsBeforeStart() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        boolean active = schedule.isActive(now);
        
        // then
        assertFalse(active);
    }

    @Test
    @DisplayName("현재 시각이 종료 후이면 isActive는 false를 반환한다")
    void isActiveReturnsFalseWhenNowIsAfterEnd() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(25);
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        boolean active = schedule.isActive(now);
        
        // then
        assertFalse(active);
    }

    @Test
    @DisplayName("현재 시각이 시작 시각과 같으면 isActive는 true를 반환한다")
    void isActiveReturnsTrueWhenNowEqualsStartsAt() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        boolean active = schedule.isActive(startsAt);
        
        // then
        assertTrue(active);
    }

    @Test
    @DisplayName("현재 시각이 종료 시각과 같으면 isActive는 false를 반환한다")
    void isActiveReturnsFalseWhenNowEqualsEndsAt() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        boolean active = schedule.isActive(endsAt);
        
        // then
        assertFalse(active);
    }

    @Test
    @DisplayName("현재 시각이 시작 시각 이후면 hasStarted는 true를 반환한다")
    void hasStartedReturnsTrueWhenNowIsAfterStart() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(1);
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        boolean started = schedule.hasStarted(now);
        
        // then
        assertTrue(started);
    }

    @Test
    @DisplayName("현재 시각이 시작 시각 이전이면 hasStarted는 false를 반환한다")
    void hasStartedReturnsFalseWhenNowIsBeforeStart() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        boolean started = schedule.hasStarted(now);
        
        // then
        assertFalse(started);
    }

    @Test
    @DisplayName("현재 시각이 시작 시각과 같으면 hasStarted는 true를 반환한다")
    void hasStartedReturnsTrueWhenNowEqualsStartsAt() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        ZonedDateTime endsAt = startsAt.plusHours(24);
        Schedule schedule = new Schedule(startsAt, endsAt, "Asia/Seoul");
        
        // when
        boolean started = schedule.hasStarted(startsAt);
        
        // then
        assertTrue(started);
    }
}


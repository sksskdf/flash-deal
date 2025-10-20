package com.flashdeal.app.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DealStatus Enum 테스트")
class DealStatusTest {

    @Test
    @DisplayName("모든 DealStatus 값을 확인할 수 있다")
    void verifyAllDealStatusValues() {
        // given & when
        DealStatus[] statuses = DealStatus.values();
        
        // then
        assertEquals(4, statuses.length);
        assertTrue(contains(statuses, DealStatus.UPCOMING));
        assertTrue(contains(statuses, DealStatus.ACTIVE));
        assertTrue(contains(statuses, DealStatus.SOLDOUT));
        assertTrue(contains(statuses, DealStatus.ENDED));
    }

    @Test
    @DisplayName("UPCOMING 상태는 시작 전을 의미한다")
    void upcomingMeansBeforeStart() {
        // given
        DealStatus status = DealStatus.UPCOMING;
        
        // then
        assertEquals("UPCOMING", status.name());
    }

    @Test
    @DisplayName("ACTIVE 상태는 진행 중을 의미한다")
    void activeMeansInProgress() {
        // given
        DealStatus status = DealStatus.ACTIVE;
        
        // then
        assertEquals("ACTIVE", status.name());
    }

    @Test
    @DisplayName("SOLDOUT 상태는 품절을 의미한다")
    void soldoutMeansSoldOut() {
        // given
        DealStatus status = DealStatus.SOLDOUT;
        
        // then
        assertEquals("SOLDOUT", status.name());
    }

    @Test
    @DisplayName("ENDED 상태는 종료를 의미한다")
    void endedMeansEnded() {
        // given
        DealStatus status = DealStatus.ENDED;
        
        // then
        assertEquals("ENDED", status.name());
    }

    @Test
    @DisplayName("UPCOMING에서 ACTIVE로 전이할 수 있다")
    void canTransitionFromUpcomingToActive() {
        // given
        DealStatus from = DealStatus.UPCOMING;
        DealStatus to = DealStatus.ACTIVE;
        
        // then
        assertTrue(from.canTransitionTo(to));
    }

    @Test
    @DisplayName("ACTIVE에서 SOLDOUT으로 전이할 수 있다")
    void canTransitionFromActiveToSoldout() {
        // given
        DealStatus from = DealStatus.ACTIVE;
        DealStatus to = DealStatus.SOLDOUT;
        
        // then
        assertTrue(from.canTransitionTo(to));
    }

    @Test
    @DisplayName("ACTIVE에서 ENDED로 전이할 수 있다")
    void canTransitionFromActiveToEnded() {
        // given
        DealStatus from = DealStatus.ACTIVE;
        DealStatus to = DealStatus.ENDED;
        
        // then
        assertTrue(from.canTransitionTo(to));
    }

    @Test
    @DisplayName("UPCOMING에서 SOLDOUT으로 직접 전이할 수 없다")
    void cannotTransitionFromUpcomingToSoldout() {
        // given
        DealStatus from = DealStatus.UPCOMING;
        DealStatus to = DealStatus.SOLDOUT;
        
        // then
        assertFalse(from.canTransitionTo(to));
    }

    @Test
    @DisplayName("ENDED에서 다른 상태로 전이할 수 없다")
    void cannotTransitionFromEnded() {
        // given
        DealStatus from = DealStatus.ENDED;
        
        // then
        assertFalse(from.canTransitionTo(DealStatus.UPCOMING));
        assertFalse(from.canTransitionTo(DealStatus.ACTIVE));
        assertFalse(from.canTransitionTo(DealStatus.SOLDOUT));
    }

    @Test
    @DisplayName("SOLDOUT에서 다른 상태로 전이할 수 없다")
    void cannotTransitionFromSoldout() {
        // given
        DealStatus from = DealStatus.SOLDOUT;
        
        // then
        assertFalse(from.canTransitionTo(DealStatus.UPCOMING));
        assertFalse(from.canTransitionTo(DealStatus.ACTIVE));
        assertFalse(from.canTransitionTo(DealStatus.ENDED));
    }

    @Test
    @DisplayName("같은 상태로 전이할 수 없다")
    void cannotTransitionToSameState() {
        // then
        assertFalse(DealStatus.UPCOMING.canTransitionTo(DealStatus.UPCOMING));
        assertFalse(DealStatus.ACTIVE.canTransitionTo(DealStatus.ACTIVE));
        assertFalse(DealStatus.SOLDOUT.canTransitionTo(DealStatus.SOLDOUT));
        assertFalse(DealStatus.ENDED.canTransitionTo(DealStatus.ENDED));
    }

    private boolean contains(DealStatus[] statuses, DealStatus status) {
        for (DealStatus s : statuses) {
            if (s == status) {
                return true;
            }
        }
        return false;
    }
}


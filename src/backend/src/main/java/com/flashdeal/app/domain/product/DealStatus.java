package com.flashdeal.app.domain.product;

import java.util.EnumSet;
import java.util.Set;

/**
 * 딜 상태 Enum
 * 
 * 전이 규칙:
 * - UPCOMING → ACTIVE (시작 시각 도달)
 * - ACTIVE → SOLDOUT (재고 0)
 * - ACTIVE → ENDED (종료 시각 도달)
 */
public enum DealStatus {
    UPCOMING,
            ACTIVE,
            SOLDOUT,
            ENDED;

    public boolean canTransitionTo(DealStatus targetStatus) {
        if (this == targetStatus) {
            return false;
        }
        
        switch (this) {
            case UPCOMING:
                return targetStatus == ACTIVE;
            case ACTIVE:
                return targetStatus == SOLDOUT || targetStatus == ENDED;
            case SOLDOUT:
            case ENDED:
                return false;
            default:
                return false;
        }
    }

    public Set<DealStatus> getAllowedTransitions() {
        switch (this) {
            case UPCOMING:
                return EnumSet.of(ACTIVE);
            case ACTIVE:
                return EnumSet.of(SOLDOUT, ENDED);
            case SOLDOUT:
            case ENDED:
                return EnumSet.noneOf(DealStatus.class);
            default:
                return EnumSet.noneOf(DealStatus.class);
        }
    }
}


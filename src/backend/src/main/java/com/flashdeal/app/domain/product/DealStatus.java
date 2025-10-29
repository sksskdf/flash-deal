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
    /**
     * 시작 전
     */
    UPCOMING,
    
    /**
     * 진행 중
     */
    ACTIVE,
    
    /**
     * 품절
     */
    SOLDOUT,
    
    /**
     * 종료
     */
    ENDED;

    /**
     * 주어진 상태로 전이 가능한지 확인
     */
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

    /**
     * 허용된 전이 상태 목록 반환
     */
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


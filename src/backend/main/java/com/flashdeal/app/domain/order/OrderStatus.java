package com.flashdeal.app.domain.order;

/**
 * 주문 상태 Enum
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}


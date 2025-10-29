package com.flashdeal.app.domain.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order Aggregate Root
 * 
 * 책임:
 * - 주문 생성 및 관리
 * - 상태 전이
 * - 금액 계산
 * - 취소/환불
 */
public class Order {
    
    private static final BigDecimal DEFAULT_SHIPPING_FEE = new BigDecimal("3000");
    
    private final OrderId orderId;
    private final UserId userId;
    private final List<OrderItem> items;
    private final Shipping shipping;
    private Pricing pricing;
    private Payment payment;
    private OrderStatus status;

    public Order(OrderId orderId, UserId userId, List<OrderItem> items, Shipping shipping) {
        validateNotNull(orderId, "OrderId cannot be null");
        validateNotNull(userId, "UserId cannot be null");
        validateNotNull(items, "Items cannot be null");
        validateNotNull(shipping, "Shipping cannot be null");
        
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be empty");
        }
        
        this.orderId = orderId;
        this.userId = userId;
        this.items = new ArrayList<>(items);
        this.shipping = shipping;
        this.status = OrderStatus.PENDING;
        
        // 초기 가격 계산
        this.pricing = calculatePricing(BigDecimal.ZERO);
        
        // 초기 결제 정보
        this.payment = new Payment("CreditCard", PaymentStatus.PENDING, null, null);
    }

    private void validateNotNull(Object value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private Pricing calculatePricing(BigDecimal discount) {
        BigDecimal subtotal = items.stream()
            .map(OrderItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        String currency = items.get(0).getSnapshot().getPrice().getCurrency();
        
        return new Pricing(subtotal, DEFAULT_SHIPPING_FEE, discount, currency);
    }

    /**
     * 주문 확정
     */
    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }

    /**
     * 배송 시작
     */
    public void ship() {
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * 배송 완료
     */
    public void deliver() {
        this.status = OrderStatus.DELIVERED;
    }

    /**
     * 주문 취소
     */
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * 환불
     */
    public void refund() {
        this.status = OrderStatus.REFUNDED;
        this.payment = payment.refund();
    }

    /**
     * 결제 완료
     */
    public void completePayment(String transactionId) {
        this.payment = payment.complete(transactionId);
    }

    /**
     * 결제 실패
     */
    public void failPayment() {
        this.payment = payment.fail();
    }

    /**
     * 할인 적용
     */
    public void applyDiscount(BigDecimal discount) {
        this.pricing = calculatePricing(discount);
    }

    // Getters
    public OrderId getOrderId() {
        return orderId;
    }

    public UserId getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Shipping getShipping() {
        return shipping;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public Payment getPayment() {
        return payment;
    }

    public OrderStatus getStatus() {
        return status;
    }

    /**
     * 상태 전이
     */
    public void transitionTo(OrderStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * 주문 번호 생성 (임시 구현)
     */
    public String getOrderNumber() {
        return "ORD-" + orderId.getValue().substring(0, 8);
    }

    /**
     * 멱등성 키 생성 (임시 구현)
     */
    public String getIdempotencyKey() {
        return "IDEMP-" + orderId.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", status=" + status +
                ", total=" + pricing.getTotal() +
                '}';
    }
}


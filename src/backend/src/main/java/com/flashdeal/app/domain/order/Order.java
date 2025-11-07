package com.flashdeal.app.domain.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    
    private static final BigDecimal DEFAULT_SHIPPING_FEE = new BigDecimal("3000");
    
    private final OrderId orderId;
    private final UserId userId;
    private final String idempotencyKey;
    private final List<OrderItem> items;
    private final Shipping shipping;
    private Pricing pricing;
    private Payment payment;
    private OrderStatus status;
    private Cancellation cancellation;
    private final Instant createdAt;


    public Order(OrderId orderId, UserId userId, List<OrderItem> items, Shipping shipping, String idempotencyKey) {
        this(orderId, userId, items, shipping, idempotencyKey, Instant.now());
    }

    public Order(OrderId orderId, UserId userId, List<OrderItem> items, Shipping shipping, String idempotencyKey, Instant createdAt) {
        validateNotNull(orderId, "OrderId cannot be null");
        validateNotNull(userId, "UserId cannot be null");
        validateNotNull(items, "Items cannot be null");
        validateNotNull(shipping, "Shipping cannot be null");
        validateNotNull(idempotencyKey, "IdempotencyKey cannot be null");
        validateNotNull(createdAt, "CreatedAt cannot be null");

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be empty");
        }
        
        this.orderId = orderId;
        this.userId = userId;
        this.items = new ArrayList<>(items);
        this.shipping = shipping;
        this.idempotencyKey = idempotencyKey;
        this.status = OrderStatus.PENDING;
        this.createdAt = createdAt;
        
        this.pricing = calculatePricing(BigDecimal.ZERO);
        
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

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }

    public void ship() {
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        cancel("User requested", "system");
    }

    public void cancel(String reason, String cancelledBy) {
        this.status = OrderStatus.CANCELLED;
        List<String> cancelledItemIds = items.stream()
                .map(item -> item.getProductId().getValue())
                .collect(java.util.stream.Collectors.toList());
        this.cancellation = new Cancellation(reason, cancelledBy, cancelledItemIds);
    }

    public void refund() {
        this.status = OrderStatus.REFUNDED;
        this.payment = payment.refund();
    }

    public void completePayment(String transactionId) {
        this.payment = payment.complete(transactionId);
    }

    public void failPayment() {
        this.payment = payment.fail();
    }

    public void applyDiscount(BigDecimal discount) {
        this.pricing = calculatePricing(discount);
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void transitionTo(OrderStatus newStatus) {
        this.status = newStatus;
    }

    public String getOrderNumber() {
        return "ORD-" + orderId.getValue().substring(0, 8);
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Cancellation getCancellation() {
        return cancellation;
    }

    public void setCancellation(Cancellation cancellation) {
        this.cancellation = cancellation;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
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


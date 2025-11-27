package com.flashdeal.app.domain.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record Order(
        OrderId orderId,
        UserId userId,
        String idempotencyKey,
        List<OrderItem> items,
        Shipping shipping,
        Pricing pricing,
        Payment payment,
        OrderStatus status,
        Cancellation cancellation,
        Instant createdAt) {
    private static final BigDecimal DEFAULT_SHIPPING_FEE = new BigDecimal("3000");
    
    public Order {
        validateNotNull(orderId, "OrderId cannot be null");
        validateNotNull(userId, "UserId cannot be null");
        validateNotNull(items, "Items cannot be null");
        validateNotNull(shipping, "Shipping cannot be null");
        validateNotNull(idempotencyKey, "IdempotencyKey cannot be null");
        validateNotNull(createdAt, "CreatedAt cannot be null");
    }

    public static Order create(OrderId orderId, UserId userId, List<OrderItem> items, Shipping shipping,
            String idempotencyKey) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be null or empty");
        }

        Pricing pricing = calculateInitialPricing(items);
        Payment payment = new Payment("CARD", PaymentStatus.PENDING, null, null);

        return new Order(
                orderId,
                userId,
                idempotencyKey,
                items,
                shipping,
                pricing,
                payment,
                OrderStatus.PENDING,
                null,
                Instant.now());
    }

    private static Pricing calculateInitialPricing(List<OrderItem> items) {
        BigDecimal subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String currency = items.get(0).snapshot().price().currency();

        return new Pricing(subtotal, DEFAULT_SHIPPING_FEE, BigDecimal.ZERO, currency);
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
        
        String currency = items.get(0).snapshot().price().currency();
        
        return new Pricing(subtotal, pricing != null ? pricing.shipping() : DEFAULT_SHIPPING_FEE, discount,
                currency);
    }

    public Order confirm() {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, OrderStatus.CONFIRMED,
                cancellation, createdAt);
    }

    public Order ship() {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, OrderStatus.SHIPPED,
                cancellation, createdAt);
    }

    public Order deliver() {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, OrderStatus.DELIVERED,
                cancellation, createdAt);
    }

    public Order cancel() {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, OrderStatus.CANCELLED,
                cancellation, createdAt);
    }

    public Order cancel(String reason, String cancelledBy) {
        List<String> cancelledItemIds = items.stream()
                .map(OrderItem::productId)
                .map(p -> p.value())
                .collect(java.util.stream.Collectors.toList());
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, OrderStatus.CANCELLED,
                new Cancellation(reason, cancelledBy, cancelledItemIds), createdAt);
    }

    public Order refund() {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment.refund(),
                OrderStatus.REFUNDED, cancellation, createdAt);
    }

    public Order completePayment(String transactionId) {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment.complete(transactionId),
                OrderStatus.CONFIRMED, cancellation, createdAt);
    }

    public Order failPayment() {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment.fail(),
                OrderStatus.CONFIRMED, cancellation, createdAt);
    }

    public Order applyDiscount(BigDecimal discount) {
        return new Order(orderId, userId, idempotencyKey, items, shipping, calculatePricing(discount), payment,
                OrderStatus.CONFIRMED, cancellation, createdAt);
    }

    public Order transitionTo(OrderStatus newStatus) {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, newStatus, cancellation,
                createdAt);
    }

    public String getOrderNumber() {
        return "ORD-" + orderId.value().substring(0, 8);
    }

    public Order setCancellation(Cancellation cancellation) {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, status, cancellation,
                createdAt);
    }

    public Order setPricing(Pricing pricing) {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, status, cancellation,
                createdAt);
    }

    public Order setPayment(Payment payment) {
        return new Order(orderId, userId, idempotencyKey, items, shipping, pricing, payment, status, cancellation,
                createdAt);
    }
}


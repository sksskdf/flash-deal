package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.inventory.Quantity;
import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.UserId;
import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CreateOrderUseCase {
    
    Mono<Order> createOrder(CreateOrderCommand command);
    
    record CreateOrderCommand(
        UserId userId,
        List<OrderItemDto> items,
        ShippingDto shipping,
        String idempotencyKey,
        java.math.BigDecimal discount
    ) {
        public CreateOrderCommand {
            if (userId == null) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("Items are required");
            }
            if (shipping == null) {
                throw new IllegalArgumentException("Shipping information is required");
            }
            if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Idempotency key is required");
            }
            if (discount == null || discount.compareTo(java.math.BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Discount must be non-negative");
            }
        }
    }
    
    record OrderItemDto(
        ProductId productId,
        Quantity quantity
    ) {
        public OrderItemDto {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID is required");
            }
            if (quantity.value() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }
    }
    
    record ShippingDto(
        String recipientName,
        String phoneNumber,
        String postalCode,
        String street,
        String city,
        String state,
        String country,
        String detailAddress
    ) {
        public ShippingDto {
            if (recipientName == null || recipientName.trim().isEmpty()) {
                throw new IllegalArgumentException("Recipient name is required");
            }
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Phone number is required");
            }
            if (postalCode == null || postalCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Postal code is required");
            }
            if (street == null || street.trim().isEmpty()) {
                throw new IllegalArgumentException("Street is required");
            }
            if (city == null || city.trim().isEmpty()) {
                throw new IllegalArgumentException("City is required");
            }
            if (country == null || country.trim().isEmpty()) {
                throw new IllegalArgumentException("Country is required");
            }
        }
    }
}

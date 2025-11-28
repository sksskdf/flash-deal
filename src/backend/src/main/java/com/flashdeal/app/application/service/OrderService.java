package com.flashdeal.app.application.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flashdeal.app.application.port.in.CancelOrderUseCase;
import com.flashdeal.app.application.port.in.CompletePaymentUseCase;
import com.flashdeal.app.application.port.in.CreateOrderUseCase;
import com.flashdeal.app.application.port.in.GetOrderUseCase;
import com.flashdeal.app.application.port.out.InventoryRepository;
import com.flashdeal.app.application.port.out.OrderRepository;
import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.Quantity;
import com.flashdeal.app.domain.order.Address;
import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.order.OrderItem;
import com.flashdeal.app.domain.order.OrderStatus;
import com.flashdeal.app.domain.order.Recipient;
import com.flashdeal.app.domain.order.Shipping;
import com.flashdeal.app.domain.order.Snapshot;
import com.flashdeal.app.domain.order.UserId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Order Application Service
 */
@Service
public class OrderService implements
        CreateOrderUseCase,
        GetOrderUseCase,
        CancelOrderUseCase,
        CompletePaymentUseCase {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public Mono<Order> createOrder(CreateOrderCommand command) {
        return orderRepository.findByIdempotencyKey(command.idempotencyKey())
                .switchIfEmpty(createNewOrder(command));
    }

    private Mono<Order> createNewOrder(CreateOrderCommand command) {
        OrderId orderId = new OrderId(UUID.randomUUID().toString());

        return createOrderItems(command.items())
                .collectList()
                .flatMap(orderItems -> {
                    Shipping shipping = createShipping(command.shipping());
                    Order order = Order.create(
                            orderId,
                            command.userId(),
                            orderItems,
                            shipping,
                            command.idempotencyKey());
                    order = order.applyDiscount(command.discount());

                    return reserveInventory(command.items())
                            .then(orderRepository.save(order));
                });
    }

    private Flux<OrderItem> createOrderItems(List<OrderItemDto> items) {
        return Flux.fromIterable(items)
                .flatMap(item -> productRepository.findById(item.productId())
                        .switchIfEmpty(
                                Mono.error(new IllegalArgumentException("Product not found: " + item.productId())))
                        .map(product -> {
                            Snapshot snapshot = new Snapshot(
                                    product.title(),
                                    product.specs().get("imageUrl") != null ? product.specs().get("imageUrl").toString()
                                            : "",
                                    product.price(),
                                    Map.of());

                            return new OrderItem(
                                    item.productId(),
                                    snapshot,
                                    item.quantity());
                        }));
    }

    private Shipping createShipping(ShippingDto dto) {
        Recipient recipient = new Recipient(
                dto.recipientName(),
                dto.phoneNumber());

        Address address = new Address(
                dto.street(),
                dto.city(),
                dto.postalCode(),
                dto.country());

        return new Shipping(
                "Standard",
                recipient,
                address,
                dto.detailAddress());
    }

    private Mono<Void> reserveInventory(List<OrderItemDto> items) {
        return Flux.fromIterable(items)
                .flatMap(item -> inventoryRepository.findByProductId(item.productId())
                        .switchIfEmpty(Mono.error(
                                new IllegalArgumentException("Inventory not found for product: " + item.productId())))
                        .flatMap(inventory -> {
                            validateInventoryForPurchase(inventory, item.quantity());
                                                inventory.reserve(item.quantity());
                            return inventoryRepository.save(inventory);
                        }))
                .then();
    }

    private void validateInventoryForPurchase(Inventory inventory, Quantity quantity) {
            if (!inventory.policy().isValidPurchaseQuantity(quantity)) {
            throw new IllegalArgumentException("Invalid purchase quantity: " + quantity);
        }
        if (inventory.stock().outOfStock()) {
            throw new IllegalStateException("Product is out of stock");
        }
    }

    @Override
    public Mono<Order> getOrder(OrderId orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Order not found: " + orderId)));
    }

    @Override
    public Flux<Order> getOrdersByUserId(UserId userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public Flux<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Flux<Order> getOrdersByUserIdAndStatus(UserId userId, OrderStatus status) {
        return orderRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    @Transactional
    public Mono<Order> cancelOrder(CancelOrderCommand command) {
        return orderRepository.findById(command.orderId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Order not found: " + command.orderId())))
                .flatMap(order -> {
                    if (order.status() != OrderStatus.PENDING) {
                        return Mono.error(new IllegalStateException(
                                "Cannot cancel order with status: " + order.status()));
                    }

                    String reason = command.reason() != null ? command.reason() : "User requested";
                    String cancelledBy = order.userId().value();
                    Order cancelledOrder = order.cancel(reason, cancelledBy);

                    return releaseInventory(cancelledOrder.items())
                            .then(orderRepository.save(cancelledOrder));
                });
    }

    private Mono<Void> releaseInventory(List<OrderItem> items) {
        return Flux.fromIterable(items)
                .flatMap(item -> inventoryRepository.findByProductId(item.productId())
                        .flatMap(inventory -> {
                                                inventory.release(item.quantity());
                            return inventoryRepository.save(inventory);
                        }))
                .then();
    }

    @Override
    @Transactional
    public Mono<Order> completePayment(CompletePaymentCommand command) {
        return orderRepository.findById(command.orderId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Order not found: " + command.orderId())))
                .flatMap(order -> {
                    if (order.status() != OrderStatus.PENDING) {
                        return Mono.error(new IllegalStateException(
                                "Cannot complete payment for order with status: " + order.status()));
                    }

                    Order completedOrder = order.completePayment(command.transactionId());
                    Order confirmedOrder = completedOrder.confirm();

                    return confirmInventory(confirmedOrder.items())
                            .then(orderRepository.save(confirmedOrder));
                });
    }

    private Mono<Void> confirmInventory(List<OrderItem> items) {
        return Flux.fromIterable(items)
                .flatMap(item -> inventoryRepository.findByProductId(item.productId())
                        .flatMap(inventory -> {
                                                inventory.confirm(item.quantity());
                            return inventoryRepository.save(inventory);
                        }))
                .then();
    }
}

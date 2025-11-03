package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.application.port.out.InventoryRepository;
import com.flashdeal.app.application.port.out.OrderRepository;
import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.order.*;
import com.flashdeal.app.domain.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        // 멱등성 체크
        return orderRepository.findByIdempotencyKey(command.idempotencyKey())
            .flatMap(existingOrder -> Mono.<Order>just(existingOrder))
            .switchIfEmpty(Mono.defer(() -> {
                OrderId orderId = new OrderId(UUID.randomUUID().toString());
                
                // 주문 아이템 생성
                return createOrderItems(command.items())
                    .collectList()
                    .flatMap(orderItems -> {
                        // 배송 정보 생성
                        Shipping shipping = createShipping(command.shipping());
                        
                        // 주문 생성
                        Order order = new Order(
                            orderId,
                            command.userId(),
                            orderItems,
                            shipping,
                            command.idempotencyKey()
                        );
                        
                        order.applyDiscount(command.discount());
                        
                        // 재고 예약
                        return reserveInventory(command.items())
                            .then(orderRepository.save(order));
                    });
            }));
    }
    
    private Flux<OrderItem> createOrderItems(List<OrderItemDto> items) {
        return Flux.fromIterable(items)
            .flatMap(item -> 
                productRepository.findById(item.productId())
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Product not found: " + item.productId())))
                    .map(product -> {
                        Snapshot snapshot = new Snapshot(
                            product.getTitle(),
                            product.getSpecs().get("imageUrl") != null ? product.getSpecs().get("imageUrl").toString() : "",
                            product.getPrice(),
                            Map.of()
                        );
                        
                        return new OrderItem(
                            item.productId(),
                            snapshot,
                            item.quantity()
                        );
                    })
            );
    }
    
    private Shipping createShipping(ShippingDto dto) {
        Recipient recipient = new Recipient(
            dto.recipientName(),
            dto.phoneNumber()
        );
        
        Address address = new Address(
            dto.street(),
            dto.city(),
            dto.postalCode(),
            dto.country()
        );
        
        return new Shipping(
            "Standard",
            recipient,
            address,
            dto.detailAddress()
        );
    }
    
    private Mono<Void> reserveInventory(List<OrderItemDto> items) {
        return Flux.fromIterable(items)
            .flatMap(item ->
                inventoryRepository.findByProductId(item.productId())
                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Inventory not found for product: " + item.productId())))
                    .flatMap(inventory -> {
                        if (inventory.isValidPurchaseQuantity(item.quantity()) == false) {
                            return Mono.error(new IllegalArgumentException(
                                "Invalid purchase quantity: " + item.quantity()
                            ));
                        }
                        
                        if (inventory.isOutOfStock()) {
                            return Mono.error(new IllegalStateException("Product is out of stock"));
                        }
                        
                        inventory.reserve(item.quantity());
                        return inventoryRepository.save(inventory);
                    })
            )
            .then();
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
                if (order.getStatus() != OrderStatus.PENDING) {
                    return Mono.error(new IllegalStateException(
                        "Cannot cancel order with status: " + order.getStatus()
                    ));
                }
                
                order.cancel();
                
                // 재고 복구
                return releaseInventory(order.getItems())
                    .then(orderRepository.save(order));
            });
    }
    
    private Mono<Void> releaseInventory(List<OrderItem> items) {
        return Flux.fromIterable(items)
            .flatMap(item ->
                inventoryRepository.findByProductId(item.getProductId())
                    .flatMap(inventory -> {
                        inventory.release(item.getQuantity());
                        return inventoryRepository.save(inventory);
                    })
            )
            .then();
    }
    
    @Override
    @Transactional
    public Mono<Order> completePayment(CompletePaymentCommand command) {
        return orderRepository.findById(command.orderId())
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Order not found: " + command.orderId())))
            .flatMap(order -> {
                if (order.getStatus() != OrderStatus.PENDING) {
                    return Mono.error(new IllegalStateException(
                        "Cannot complete payment for order with status: " + order.getStatus()
                    ));
                }
                
                order.completePayment(command.transactionId());
                order.confirm();
                
                // 재고 확정
                return confirmInventory(order.getItems())
                    .then(orderRepository.save(order));
            });
    }
    
    private Mono<Void> confirmInventory(List<OrderItem> items) {
        return Flux.fromIterable(items)
            .flatMap(item ->
                inventoryRepository.findByProductId(item.getProductId())
                    .flatMap(inventory -> {
                        inventory.confirm(item.getQuantity());
                        return inventoryRepository.save(inventory);
                    })
            )
            .then();
    }
}

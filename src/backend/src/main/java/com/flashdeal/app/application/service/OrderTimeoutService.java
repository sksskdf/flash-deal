package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.CancelOrderUseCase;
import com.flashdeal.app.application.port.out.OrderRepository;
import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Service
public class OrderTimeoutService {

    private static final Duration ORDER_TIMEOUT = Duration.ofMinutes(10);

    private final OrderRepository orderRepository;
    private final CancelOrderUseCase cancelOrderUseCase;

    public OrderTimeoutService(OrderRepository orderRepository, CancelOrderUseCase cancelOrderUseCase) {
        this.orderRepository = orderRepository;
        this.cancelOrderUseCase = cancelOrderUseCase;
    }

    @Scheduled(fixedRate = 60000)
    public void cancelTimedOutOrders() {
        Instant timeout = Instant.now().minus(ORDER_TIMEOUT);
        orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.PENDING, timeout)
                .flatMap(this::cancelOrder)
                .subscribe();
    }

    private Mono<Order> cancelOrder(Order order) {
        return cancelOrderUseCase.cancelOrder(new CancelOrderUseCase.CancelOrderCommand(order.orderId(), "Order timed out"));
    }
}

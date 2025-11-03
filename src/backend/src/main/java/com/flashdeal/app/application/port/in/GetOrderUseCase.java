package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.order.UserId;
import com.flashdeal.app.domain.order.OrderStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetOrderUseCase {
    
    Mono<Order> getOrder(OrderId orderId);
    
    Flux<Order> getOrdersByUserId(UserId userId);
    
    Flux<Order> getOrdersByStatus(OrderStatus status);
    
    Flux<Order> getOrdersByUserIdAndStatus(UserId userId, OrderStatus status);
}

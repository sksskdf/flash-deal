package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderId;
import reactor.core.publisher.Mono;

public interface CancelOrderUseCase {
    
    Mono<Order> cancelOrder(CancelOrderCommand command);
    
    record CancelOrderCommand(
        OrderId orderId,
        String reason
    ) {
        public CancelOrderCommand {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID is required");
            }
        }
    }
}

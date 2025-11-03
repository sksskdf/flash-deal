package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderId;
import reactor.core.publisher.Mono;

public interface CompletePaymentUseCase {
    
    Mono<Order> completePayment(CompletePaymentCommand command);
    
    record CompletePaymentCommand(
        OrderId orderId,
        String transactionId
    ) {
        public CompletePaymentCommand {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID is required");
            }
            if (transactionId == null || transactionId.trim().isEmpty()) {
                throw new IllegalArgumentException("Transaction ID is required");
            }
        }
    }
}

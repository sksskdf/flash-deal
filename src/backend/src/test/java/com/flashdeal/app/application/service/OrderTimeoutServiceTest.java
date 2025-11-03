package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.CancelOrderUseCase;
import com.flashdeal.app.application.port.out.OrderRepository;
import com.flashdeal.app.domain.order.*;
import com.flashdeal.app.domain.product.ProductId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTimeoutServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    CancelOrderUseCase cancelOrderUseCase;

    @InjectMocks
    OrderTimeoutService timeoutService;

    @Test
    void cancelTimedOutOrders_invokesCancelForPending() {
        Order pending = new Order(new OrderId("O-1"), new UserId("U-1"),
            List.of(new OrderItem(new ProductId("P-1"), new Snapshot("t","", new com.flashdeal.app.domain.product.Price(java.math.BigDecimal.ONE, java.math.BigDecimal.ONE, "KRW"), java.util.Map.of()), 1)),
            new Shipping("Standard", new Recipient("n","p"), new Address("s","c","z","KR"), null), "idem", Instant.now().minusSeconds(3600)
        );

        when(orderRepository.findByStatusAndCreatedAtBefore(eq(OrderStatus.PENDING), any())).thenReturn(Flux.just(pending));
        when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Mono.just(pending));

        timeoutService.cancelTimedOutOrders();

        verify(cancelOrderUseCase, atLeastOnce()).cancelOrder(any());
    }
}



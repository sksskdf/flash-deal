package com.flashdeal.app.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flashdeal.app.application.port.in.CancelOrderUseCase;
import com.flashdeal.app.application.port.out.OrderRepository;
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
import com.flashdeal.app.domain.product.Price;
import com.flashdeal.app.domain.product.ProductId;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderTimeoutService 테스트")
class OrderTimeoutServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    CancelOrderUseCase cancelOrderUseCase;

    @InjectMocks
    OrderTimeoutService timeoutService;

    @Test
    @DisplayName("타임아웃된 주문에 대해 취소를 호출한다")
    void cancelTimedOutOrders_invokesCancelForPending() {
        Order pending = Order.create(new OrderId("O-1"), new UserId("U-1"),
                List.of(new OrderItem(new ProductId("P-1"),
                        new Snapshot("t", "", new Price(new BigDecimal("1"), new BigDecimal("1"), "KRW"), Map.of()),
                        new Quantity(1))),
                new Shipping("Standard", new Recipient("n", "p"), new Address("s", "c", "z", "KR"), null), "idem");

        when(orderRepository.findByStatusAndCreatedAtBefore(eq(OrderStatus.PENDING), any()))
                .thenReturn(Flux.just(pending));
        when(cancelOrderUseCase.cancelOrder(any())).thenReturn(Mono.just(pending));

        timeoutService.cancelTimedOutOrders();

        verify(cancelOrderUseCase, atLeastOnce()).cancelOrder(any());
    }
}

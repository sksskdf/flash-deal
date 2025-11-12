package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.TestDataFactory;
import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.order.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderResolver 테스트")
class OrderResolverTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private GetOrderUseCase getOrderUseCase;

    @Mock
    private CancelOrderUseCase cancelOrderUseCase;

    @Mock
    private CompletePaymentUseCase completePaymentUseCase;

    @InjectMocks
    private OrderResolver orderResolver;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = TestDataFactory.createOrder();
    }

    @Test
    @DisplayName("order - 주문 조회 성공")
    void order_success() {
        // Given
        String orderId = testOrder.orderId().value();
        given(getOrderUseCase.getOrder(any(OrderId.class)))
            .willReturn(Mono.just(testOrder));

        // When
        Mono<Order> result = orderResolver.order(orderId);

        // Then
        StepVerifier.create(result)
            .assertNext(order -> {
                    assertThat(order.orderId()).isEqualTo(testOrder.orderId());
                    assertThat(order.status()).isEqualTo(testOrder.status());
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("ordersByUser - 사용자별 주문 목록 조회")
    void ordersByUser_success() {
        // Given
        String userId = testOrder.userId().value();
        given(getOrderUseCase.getOrdersByUserId(any(UserId.class)))
            .willReturn(Flux.just(testOrder));

        // When
        Flux<Order> result = orderResolver.ordersByUser(userId);

        // Then
        StepVerifier.create(result)
                .assertNext(order -> assertThat(order.userId()).isEqualTo(testOrder.userId()))
            .verifyComplete();
    }

    @Test
    @DisplayName("orders - 상태별 주문 목록 조회")
    void orders_byStatus() {
        // Given
        given(getOrderUseCase.getOrdersByStatus(OrderStatus.PENDING))
            .willReturn(Flux.just(testOrder));

        // When
        Flux<Order> result = orderResolver.orders(OrderStatus.PENDING);

        // Then
        StepVerifier.create(result)
                .assertNext(order -> assertThat(order.status()).isEqualTo(OrderStatus.PENDING))
            .verifyComplete();
    }

    @Test
    @DisplayName("orders - 상태 파라미터 없을 때 빈 결과 반환")
    void orders_withoutStatus_returnsEmpty() {
        // When
        Flux<Order> result = orderResolver.orders(null);

        // Then
        StepVerifier.create(result)
            .verifyComplete();
    }

    @Test
    @DisplayName("createOrder - 주문 생성 성공")
    void createOrder_success() {
        // Given
        OrderResolver.CreateOrderInput input = new OrderResolver.CreateOrderInput(
            "user-123",
            java.util.List.of(
                new OrderResolver.OrderItemInput("product-123", 2)
            ),
            new OrderResolver.ShippingInput(
                "Standard",
                new OrderResolver.RecipientInput("홍길동", "+82-10-1234-5678"),
                new OrderResolver.AddressInput(
                    "테헤란로 427",
                    "서울",
                    "서울특별시",
                    "06158",
                    "KR",
                    "101동 101호"
                ),
                "문 앞에 놔주세요"
            ),
            "idempotency-key-123",
            new BigDecimal("5000")
        );

        given(createOrderUseCase.createOrder(any(CreateOrderUseCase.CreateOrderCommand.class)))
            .willReturn(Mono.just(testOrder));

        // When
        Mono<Order> result = orderResolver.createOrder(input);

        // Then
        StepVerifier.create(result)
            .assertNext(order -> assertThat(order).isNotNull())
            .verifyComplete();
    }

    @Test
    @DisplayName("createOrder - 할인 없이 주문 생성")
    void createOrder_withoutDiscount() {
        // Given
        OrderResolver.CreateOrderInput input = new OrderResolver.CreateOrderInput(
            "user-123",
            java.util.List.of(
                new OrderResolver.OrderItemInput("product-123", 2)
            ),
            new OrderResolver.ShippingInput(
                "Standard",
                new OrderResolver.RecipientInput("홍길동", "+82-10-1234-5678"),
                new OrderResolver.AddressInput(
                    "테헤란로 427",
                    "서울",
                    null,
                    "06158",
                    "KR",
                    null
                ),
                null
            ),
            "idempotency-key-123",
            null
        );

        given(createOrderUseCase.createOrder(any(CreateOrderUseCase.CreateOrderCommand.class)))
            .willReturn(Mono.just(testOrder));

        // When
        Mono<Order> result = orderResolver.createOrder(input);

        // Then
        StepVerifier.create(result)
            .assertNext(order -> assertThat(order).isNotNull())
            .verifyComplete();
    }

    @Test
    @DisplayName("cancelOrder - 주문 취소 성공")
    void cancelOrder_success() {
        // Given
        String orderId = testOrder.orderId().value();
        Order cancelledOrder = TestDataFactory.createCancelledOrder();

        given(cancelOrderUseCase.cancelOrder(any(CancelOrderUseCase.CancelOrderCommand.class)))
            .willReturn(Mono.just(cancelledOrder));

        // When
        Mono<Order> result = orderResolver.cancelOrder(orderId, "고객 요청");

        // Then
        StepVerifier.create(result)
                .assertNext(order -> assertThat(order.status()).isEqualTo(OrderStatus.CANCELLED))
            .verifyComplete();
    }

    @Test
    @DisplayName("completePayment - 결제 완료 성공")
    void completePayment_success() {
        // Given
        String orderId = testOrder.orderId().value();
        Order confirmedOrder = TestDataFactory.createConfirmedOrder();

        given(completePaymentUseCase.completePayment(any(CompletePaymentUseCase.CompletePaymentCommand.class)))
            .willReturn(Mono.just(confirmedOrder));

        // When
        Mono<Order> result = orderResolver.completePayment(orderId, "txn-abc123");

        // Then
        StepVerifier.create(result)
                .assertNext(order -> assertThat(order.status()).isEqualTo(OrderStatus.CONFIRMED))
            .verifyComplete();
    }
}


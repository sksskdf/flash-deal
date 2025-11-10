package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.CreateOrderUseCase.CreateOrderCommand;
import com.flashdeal.app.application.port.in.CreateOrderUseCase.OrderItemDto;
import com.flashdeal.app.application.port.in.CreateOrderUseCase.ShippingDto;
import com.flashdeal.app.application.port.in.CompletePaymentUseCase.CompletePaymentCommand;
import com.flashdeal.app.application.port.in.CancelOrderUseCase.CancelOrderCommand;
import com.flashdeal.app.application.port.out.InventoryRepository;
import com.flashdeal.app.application.port.out.OrderRepository;
import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.inventory.Policy;
import com.flashdeal.app.domain.inventory.Stock;
import com.flashdeal.app.domain.order.*;
import com.flashdeal.app.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 테스트")
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    InventoryRepository inventoryRepository;

    @InjectMocks
    OrderService orderService;

    Product sampleProduct;
    Inventory sampleInventory;

    @BeforeEach
    void setUp() {
        ProductId productId = new ProductId("P-1");
        Price price = new Price(new BigDecimal("10000"), new BigDecimal("9000"), "KRW");
        Schedule schedule = new Schedule(ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), "Asia/Seoul");
        Specs specs = new Specs(Map.of("imageUrl", "http://img"));
        sampleProduct = new Product(productId, "상품", "설명", price, schedule, specs);

        sampleInventory = new Inventory(
            new InventoryId("I-1"),
            productId,
            new Stock(100, 10, 90, 0),
            new Policy(1, 10, 5)
        );
    }

    @Test
    @DisplayName("주문을 생성하면 재고를 예약하고 주문을 저장한다")
    void createOrder_success_reservesInventory_andSavesOrder() {
        UserId userId = new UserId("U-1");
        List<OrderItemDto> items = List.of(new OrderItemDto(sampleProduct.getProductId(), 2));
        ShippingDto shipping = new ShippingDto(
            "홍길동", // recipientName
            "010-0000-0000", // phoneNumber
            "12345", // postalCode
            "서울시 강남구", // street
            "서울", // city
            "", // state
            "KR", // country
            "상세" // detailAddress
        );
        CreateOrderCommand cmd = new CreateOrderCommand(userId, items, shipping, "idem-1", new BigDecimal("1000"));

        given(orderRepository.findByIdempotencyKey("idem-1")).willReturn(Mono.empty());
        given(productRepository.findById(sampleProduct.getProductId())).willReturn(Mono.just(sampleProduct));
        given(inventoryRepository.findByProductId(sampleProduct.getProductId())).willReturn(Mono.just(sampleInventory));
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));
        given(orderRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        Mono<Order> result = orderService.createOrder(cmd);

        StepVerifier.create(result)
            .assertNext(order -> {
                assertThat(order.getUserId()).isEqualTo(userId);
                assertThat(order.getItems()).hasSize(1);
                    assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
                    assertThat(order.getPricing().discount()).isEqualTo(new BigDecimal("1000"));
            })
            .verifyComplete();

        verify(inventoryRepository, atLeastOnce()).save(any());
        verify(orderRepository).save(any());
    }

    @Test
    @DisplayName("멱등성 키가 있으면 기존 주문을 반환한다")
    void createOrder_idempotent_returnsExisting() {
        UserId userId = new UserId("U-1");
        List<OrderItemDto> items = List.of(new OrderItemDto(sampleProduct.getProductId(), 1));
        ShippingDto shipping = new ShippingDto(
            "홍길동", "010", "12345", "서울 강남", "서울", "", "KR", null
        );
        CreateOrderCommand cmd = new CreateOrderCommand(userId, items, shipping, "idem-dup", BigDecimal.ZERO);

        Order existing = Order.create(new OrderId("O-1"), userId, List.of(
            new OrderItem(sampleProduct.getProductId(),
                new Snapshot(sampleProduct.getTitle(), "", sampleProduct.getPrice(), Map.of()), 1)
        ), new Shipping("Standard", new Recipient("홍길동", "010"), new Address("s", "c", "z", "KR"), null), "idem-dup");

        given(orderRepository.findByIdempotencyKey("idem-dup")).willReturn(Mono.just(existing));

        StepVerifier.create(orderService.createOrder(cmd))
            .expectNext(existing)
            .verifyComplete();

        verify(orderRepository, never()).save(any());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("대기 중인 주문을 취소하면 재고를 해제하고 저장한다")
    void cancelOrder_whenPending_releasesInventory_andSaves() {
        OrderId orderId = new OrderId("O-2");
        Order pending = Order.create(orderId, new UserId("U-1"), List.of(
            new OrderItem(sampleProduct.getProductId(), new Snapshot("t", "", sampleProduct.getPrice(), Map.of()), 3)
        ), new Shipping("Standard", new Recipient("n", "p"), new Address("s","c","z","KR"), null), "idem");

        given(orderRepository.findById(orderId)).willReturn(Mono.just(pending));
        given(inventoryRepository.findByProductId(sampleProduct.getProductId())).willReturn(Mono.just(sampleInventory));
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));
        given(orderRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(orderService.cancelOrder(new CancelOrderCommand(orderId, "change of mind")))
            .assertNext(order -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED))
            .verifyComplete();

        verify(inventoryRepository, atLeastOnce()).save(any());
        verify(orderRepository).save(any());
    }

    @Test
    @DisplayName("대기 중인 주문의 결제를 완료하면 재고를 확정하고 저장한다")
    void completePayment_whenPending_confirmsInventory_andSaves() {
        OrderId orderId = new OrderId("O-3");
        Order pending = Order.create(orderId, new UserId("U-1"), List.of(
            new OrderItem(sampleProduct.getProductId(), new Snapshot("t", "", sampleProduct.getPrice(), Map.of()), 2)
        ), new Shipping("Standard", new Recipient("n", "p"), new Address("s","c","z","KR"), null), "idem");

        given(orderRepository.findById(orderId)).willReturn(Mono.just(pending));
        given(inventoryRepository.findByProductId(sampleProduct.getProductId())).willReturn(Mono.just(sampleInventory));
        given(inventoryRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));
        given(orderRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(orderService.completePayment(new CompletePaymentCommand(orderId, "tx-1")))
            .assertNext(order -> assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED))
            .verifyComplete();

        verify(inventoryRepository, atLeastOnce()).save(any());
        verify(orderRepository).save(any());
    }

    @Test
    @DisplayName("유효하지 않은 수량으로 주문 생성 시 예외가 발생한다")
    void createOrder_invalidQuantity_throws() {
        UserId userId = new UserId("U-1");
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new OrderItemDto(sampleProduct.getProductId(), 0);
        });

        List<OrderItemDto> validItems = List.of(new OrderItemDto(sampleProduct.getProductId(), 1));
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new CreateOrderCommand(userId, validItems, null, "idem", BigDecimal.ZERO);
        });
    }
}



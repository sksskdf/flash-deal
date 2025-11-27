package com.flashdeal.app.domain.order;

import org.junit.jupiter.api.Test;

import com.flashdeal.app.domain.product.Price;
import com.flashdeal.app.domain.product.ProductId;

import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Order Entity 테스트")
class OrderTest {

    @Test
    @DisplayName("유효한 값으로 Order를 생성할 수 있다")
    void createOrderWithValidValues() {
        // given
        OrderId orderId = OrderId.generate();
        UserId userId = new UserId("user123");
        List<OrderItem> items = createOrderItems();
        Shipping shipping = createShipping();
        
        // when
        Order order = Order.create(orderId, userId, items, shipping, "test-key");
        
        // then
        assertNotNull(order);
        assertEquals(orderId, order.orderId());
        assertEquals(userId, order.userId());
        assertEquals(1, order.items().size());
        assertEquals(shipping, order.shipping());
        assertEquals(OrderStatus.PENDING, order.status());
    }

    @Test
    @DisplayName("주문 생성 시 가격이 자동 계산된다")
    void pricingCalculatedOnOrderCreation() {
        // given
        List<OrderItem> items = createOrderItems();
        Shipping shipping = createShipping();
        
        // when
        Order order = Order.create(
            OrderId.generate(),
            new UserId("user123"),
            items,
            shipping,
            "test-key"
        );
        
        // then
        Pricing pricing = order.pricing();
        assertEquals(new BigDecimal("160000"), pricing.subtotal());
        assertEquals(new BigDecimal("3000"), pricing.shipping());
        assertEquals(new BigDecimal("163000"), pricing.total());
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        List<OrderItem> items = createOrderItems();
        Shipping shipping = createShipping();
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(null, new UserId("user123"), items, shipping, "test-key"));
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(OrderId.generate(), null, items, shipping, "test-key"));
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(OrderId.generate(), new UserId("user123"), null, shipping, "test-key"));
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(OrderId.generate(), new UserId("user123"), items, null, "test-key"));
    }

    @Test
    @DisplayName("주문 항목이 비어있으면 예외가 발생한다")
    void throwsExceptionWhenItemsAreEmpty() {
        // given
        Shipping shipping = createShipping();
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> Order.create(OrderId.generate(), new UserId("user123"), new ArrayList<>(), shipping, "test-key"));
    }

    @Test
    @DisplayName("주문을 확정할 수 있다")
    void canConfirmOrder() {
        // given
        Order order = createOrder();
        
        // when
        Order confirmed = order.confirm();
        
        // then
        assertEquals(OrderStatus.CONFIRMED, confirmed.status());
    }

    @Test
    @DisplayName("주문을 배송 중으로 변경할 수 있다")
    void canShipOrder() {
        // given
        Order order = createOrder();
        Order confirmed = order.confirm();
        
        // when
        Order shipped = confirmed.ship();
        
        // then
        assertEquals(OrderStatus.SHIPPED, shipped.status());
    }

    @Test
    @DisplayName("주문을 배송 완료로 변경할 수 있다")
    void canDeliverOrder() {
        // given
        Order order = createOrder();
        Order confirmed = order.confirm();
        Order shipped = confirmed.ship();
        
        // when
        Order delivered = shipped.deliver();
        
        // then
        assertEquals(OrderStatus.DELIVERED, delivered.status());
    }

    @Test
    @DisplayName("주문을 취소할 수 있다")
    void canCancelOrder() {
        // given
        Order order = createOrder();
        
        // when
        Order cancelled = order.cancel();
        
        // then
        assertEquals(OrderStatus.CANCELLED, cancelled.status());
    }

    @Test
    @DisplayName("결제를 완료할 수 있다")
    void canCompletePayment() {
        // given
        Order order = createOrder();
        
        // when
        Order completed = order.completePayment("txn_12345");
        
        // then
        assertEquals(PaymentStatus.COMPLETED, completed.payment().status());
        assertEquals("txn_12345", completed.payment().transactionId());
    }

    @Test
    @DisplayName("결제를 실패 처리할 수 있다")
    void canFailPayment() {
        // given
        Order order = createOrder();
        
        // when
        Order failed = order.failPayment();
        
        // then
        assertEquals(PaymentStatus.FAILED, failed.payment().status());
    }

    @Test
    @DisplayName("할인을 적용할 수 있다")
    void canApplyDiscount() {
        // given
        Order order = createOrder();
        
        // when
        Order discounted = order.applyDiscount(new BigDecimal("10000"));
        
        // then
        assertEquals(new BigDecimal("10000"), discounted.pricing().discount());
        assertEquals(new BigDecimal("153000"), discounted.pricing().total());
    }

    private Order createOrder() {
        return Order.create(
            OrderId.generate(),
            new UserId("user123"),
            createOrderItems(),
            createShipping(),
            "test-key"
        );
    }

    private List<OrderItem> createOrderItems() {
        List<OrderItem> items = new ArrayList<>();
        
        Price price1 = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot1 = new Snapshot(
            "AirPods Pro",
            "https://example.com/image1.jpg",
            price1,
            new HashMap<>()
        );
        items.add(new OrderItem(ProductId.generate(), snapshot1, 2));
        
        return items;
    }

    private Shipping createShipping() {
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        Address address = new Address("테헤란로 427", "서울", "06158", "KR");
        return new Shipping("Standard", recipient, address, null);
    }
}


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
        Order order = new Order(orderId, userId, items, shipping);
        
        // then
        assertNotNull(order);
        assertEquals(orderId, order.getOrderId());
        assertEquals(userId, order.getUserId());
        assertEquals(1, order.getItems().size());
        assertEquals(shipping, order.getShipping());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("주문 생성 시 가격이 자동 계산된다")
    void pricingCalculatedOnOrderCreation() {
        // given
        List<OrderItem> items = createOrderItems();
        Shipping shipping = createShipping();
        
        // when
        Order order = new Order(
            OrderId.generate(),
            new UserId("user123"),
            items,
            shipping
        );
        
        // then
        Pricing pricing = order.getPricing();
        assertEquals(new BigDecimal("160000"), pricing.getSubtotal());
        assertEquals(new BigDecimal("3000"), pricing.getShipping());
        assertEquals(new BigDecimal("163000"), pricing.getTotal());
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        List<OrderItem> items = createOrderItems();
        Shipping shipping = createShipping();
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Order(null, new UserId("user123"), items, shipping));
        assertThrows(IllegalArgumentException.class,
            () -> new Order(OrderId.generate(), null, items, shipping));
        assertThrows(IllegalArgumentException.class,
            () -> new Order(OrderId.generate(), new UserId("user123"), null, shipping));
        assertThrows(IllegalArgumentException.class,
            () -> new Order(OrderId.generate(), new UserId("user123"), items, null));
    }

    @Test
    @DisplayName("주문 항목이 비어있으면 예외가 발생한다")
    void throwsExceptionWhenItemsAreEmpty() {
        // given
        Shipping shipping = createShipping();
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Order(OrderId.generate(), new UserId("user123"), new ArrayList<>(), shipping));
    }

    @Test
    @DisplayName("주문을 확정할 수 있다")
    void canConfirmOrder() {
        // given
        Order order = createOrder();
        
        // when
        order.confirm();
        
        // then
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("주문을 배송 중으로 변경할 수 있다")
    void canShipOrder() {
        // given
        Order order = createOrder();
        order.confirm();
        
        // when
        order.ship();
        
        // then
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
    }

    @Test
    @DisplayName("주문을 배송 완료로 변경할 수 있다")
    void canDeliverOrder() {
        // given
        Order order = createOrder();
        order.confirm();
        order.ship();
        
        // when
        order.deliver();
        
        // then
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }

    @Test
    @DisplayName("주문을 취소할 수 있다")
    void canCancelOrder() {
        // given
        Order order = createOrder();
        
        // when
        order.cancel();
        
        // then
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    @DisplayName("결제를 완료할 수 있다")
    void canCompletePayment() {
        // given
        Order order = createOrder();
        
        // when
        order.completePayment("txn_12345");
        
        // then
        assertEquals(PaymentStatus.COMPLETED, order.getPayment().getStatus());
        assertEquals("txn_12345", order.getPayment().getTransactionId());
    }

    @Test
    @DisplayName("결제를 실패 처리할 수 있다")
    void canFailPayment() {
        // given
        Order order = createOrder();
        
        // when
        order.failPayment();
        
        // then
        assertEquals(PaymentStatus.FAILED, order.getPayment().getStatus());
    }

    @Test
    @DisplayName("할인을 적용할 수 있다")
    void canApplyDiscount() {
        // given
        Order order = createOrder();
        
        // when
        order.applyDiscount(new BigDecimal("10000"));
        
        // then
        assertEquals(new BigDecimal("10000"), order.getPricing().getDiscount());
        assertEquals(new BigDecimal("153000"), order.getPricing().getTotal());
    }

    private Order createOrder() {
        return new Order(
            OrderId.generate(),
            new UserId("user123"),
            createOrderItems(),
            createShipping()
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


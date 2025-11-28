package com.flashdeal.app;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.inventory.Policy;
import com.flashdeal.app.domain.inventory.Quantity;
import com.flashdeal.app.domain.inventory.Stock;
import com.flashdeal.app.domain.order.Address;
import com.flashdeal.app.domain.order.Order;
import com.flashdeal.app.domain.order.OrderId;
import com.flashdeal.app.domain.order.OrderItem;
import com.flashdeal.app.domain.order.Payment;
import com.flashdeal.app.domain.order.PaymentStatus;
import com.flashdeal.app.domain.order.Pricing;
import com.flashdeal.app.domain.order.Recipient;
import com.flashdeal.app.domain.order.Shipping;
import com.flashdeal.app.domain.order.Snapshot;
import com.flashdeal.app.domain.order.UserId;
import com.flashdeal.app.domain.product.DealStatus;
import com.flashdeal.app.domain.product.Price;
import com.flashdeal.app.domain.product.Product;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.domain.product.Schedule;
import com.flashdeal.app.domain.product.Specs;

/**
 * 테스트 데이터 팩토리 클래스
 * 모든 테스트에서 공통으로 사용할 수 있는 테스트 데이터 생성 메서드들을 제공
 */
public class TestDataFactory {

    // ========== Product 관련 ==========

    public static Product createProduct() {
        return createProduct(ProductId.generate());
    }

    public static Product createProduct(ProductId productId) {
        String title = "Test Product";
        String description = "Test Description";

        Price price = new Price(
                new BigDecimal("100000"),
                new BigDecimal("80000"),
                "KRW");

        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        Schedule schedule = new Schedule(
                startsAt,
                startsAt.plusHours(24),
                "Asia/Seoul");

        Map<String, Object> specsFields = new HashMap<>();
        specsFields.put("color", "black");
        specsFields.put("weight", "200g");
        Specs specs = new Specs(specsFields);

        return new Product(productId, title, description, "카테고리", price, schedule, specs, DealStatus.UPCOMING);
    }

    public static Product createActiveProduct() {
        Product product = createProduct();
        Product updatedProduct = product.transitionTo(DealStatus.ACTIVE);
        return updatedProduct;
    }

    public static Product createSoldOutProduct() {
        Product product = createProduct();
        Product updatedProduct = product.transitionTo(DealStatus.ACTIVE);
        Product updatedProduct2 = updatedProduct.transitionTo(DealStatus.SOLDOUT);
        return updatedProduct2;
    }

    // ========== Inventory 관련 ==========

    public static Inventory createInventory() {
        return createInventory(InventoryId.generate(), ProductId.generate());
    }

    public static Inventory createInventory(InventoryId inventoryId, ProductId productId) {
        return createInventory(inventoryId, productId, 1000);
    }

    public static Inventory createInventory(InventoryId inventoryId, ProductId productId, int totalStock) {
        Stock stock = Stock.initial(new Quantity(totalStock));
        Policy policy = Policy.defaultPolicy();

        return new Inventory(inventoryId, productId, stock, policy);
    }

    public static Inventory createLowStockInventory() {
        Inventory inventory = createInventory();
        inventory.reserve(new Quantity(995)); // available: 5 (기본 안전재고 10보다 적음)
        return inventory;
    }

    public static Inventory createOutOfStockInventory() {
        Inventory inventory = createInventory();
        inventory.reserve(new Quantity(1000)); // 모든 재고 예약
        return inventory;
    }

    // ========== Order 관련 ==========

    public static Order createOrder() {
        return createOrder(OrderId.generate(), UserId.generate());
    }

    public static Order createOrder(OrderId orderId, UserId userId) {
        List<OrderItem> items = createOrderItems();
        Shipping shipping = createShipping();

        return Order.create(orderId, userId, items, shipping, "idempotencyKey");
    }

    public static Order createOrderWithItems(List<OrderItem> items) {
        OrderId orderId = OrderId.generate();
        UserId userId = UserId.generate();
        Shipping shipping = createShipping();

        return Order.create(orderId, userId, items, shipping, "idempotencyKey");
    }

    public static Order createConfirmedOrder() {
        Order order = createOrder();
        return order.confirm();
    }

    public static Order createShippedOrder() {
        Order order = createOrder();
        Order confirmedOrder = order.confirm();
        return confirmedOrder.ship();
    }

    public static Order createDeliveredOrder() {
        Order order = createOrder();
        Order confirmedOrder = order.confirm();
        return confirmedOrder.ship().deliver();
    }

    public static Order createCancelledOrder() {
        Order order = createOrder();
        return order.cancel();
    }

    // ========== OrderItem 관련 ==========

    public static List<OrderItem> createOrderItems() {
        List<OrderItem> items = new ArrayList<>();

        Price price1 = new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW");
        Snapshot snapshot1 = new Snapshot(
                "AirPods Pro",
                "https://example.com/image1.jpg",
                price1,
                Map.of("color", "black"));
        items.add(new OrderItem(ProductId.generate(), snapshot1, new Quantity(2)));

        return items;
    }

    public static OrderItem createOrderItem(ProductId productId, Quantity quantity) {
        Price price = new Price(new BigDecimal("50000"), new BigDecimal("40000"), "KRW");
        Snapshot snapshot = new Snapshot(
                "Test Item",
                "https://example.com/test.jpg",
                price,
                Map.of("size", "M"));

        return new OrderItem(productId, snapshot, quantity);
    }

    // ========== Shipping 관련 ==========

    public static Shipping createShipping() {
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        Address address = new Address("테헤란로 427", "서울", "06158", "KR");

        return new Shipping("Standard", recipient, address, "문 앞에 놔주세요");
    }

    public static Shipping createShipping(String method) {
        Recipient recipient = new Recipient("홍길동", "+82-10-1234-5678");
        Address address = new Address("테헤란로 427", "서울", "06158", "KR");

        return new Shipping(method, recipient, address, null);
    }

    // ========== Price 관련 ==========

    public static Price createPrice() {
        return new Price(
                new BigDecimal("100000"),
                new BigDecimal("80000"),
                "KRW");
    }

    public static Price createPrice(BigDecimal original, BigDecimal sale) {
        return new Price(original, sale, "KRW");
    }

    public static Price createPriceWithCurrency(String currency) {
        return new Price(
                new BigDecimal("100000"),
                new BigDecimal("80000"),
                currency);
    }

    // ========== Schedule 관련 ==========

    public static Schedule createSchedule() {
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        return new Schedule(
                startsAt,
                startsAt.plusHours(24),
                "Asia/Seoul");
    }

    public static Schedule createActiveSchedule() {
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(1);
        return new Schedule(
                startsAt,
                startsAt.plusHours(24),
                "Asia/Seoul");
    }

    public static Schedule createEndedSchedule() {
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(25);
        return new Schedule(
                startsAt,
                startsAt.plusHours(24),
                "Asia/Seoul");
    }

    // ========== Policy 관련 ==========

    public static Policy createPolicy() {
        return Policy.defaultPolicy();
    }

    public static Policy createPolicy(int safetyStock, int reservationTimeout, int maxPurchasePerUser) {
        return new Policy(safetyStock, reservationTimeout, maxPurchasePerUser);
    }

    // ========== Stock 관련 ==========

    public static Stock createStock(int total) {
        return Stock.initial(new Quantity(total));
    }

    public static Stock createStock(int total, int reserved, int available, int sold) {
        return new Stock(new Quantity(total), new Quantity(reserved), new Quantity(available), new Quantity(sold));
    }

    // ========== Specs 관련 ==========

    public static Specs createSpecs() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("color", "black");
        fields.put("weight", "200g");
        fields.put("battery", "30h");

        return new Specs(fields);
    }

    public static Specs createSpecs(Map<String, Object> fields) {
        return new Specs(fields);
    }

    // ========== Address 관련 ==========

    public static Address createAddress() {
        return new Address("테헤란로 427", "서울", "06158", "KR");
    }

    public static Address createAddress(String street, String city, String zipCode, String country) {
        return new Address(street, city, zipCode, country);
    }

    // ========== Recipient 관련 ==========

    public static Recipient createRecipient() {
        return new Recipient("홍길동", "+82-10-1234-5678");
    }

    public static Recipient createRecipient(String name, String phone) {
        return new Recipient(name, phone);
    }

    // ========== Payment 관련 ==========

    public static Payment createPayment() {
        return new Payment("CreditCard", PaymentStatus.PENDING, null, "Stripe");
    }

    public static Payment createCompletedPayment(String transactionId) {
        return new Payment("CreditCard", PaymentStatus.COMPLETED, transactionId, "Stripe");
    }

    public static Payment createFailedPayment() {
        return new Payment("CreditCard", PaymentStatus.FAILED, null, "Stripe");
    }

    // ========== Pricing 관련 ==========

    public static Pricing createPricing() {
        return new Pricing(
                new BigDecimal("100000"),
                new BigDecimal("3000"),
                new BigDecimal("5000"),
                "KRW");
    }

    public static Pricing createPricing(BigDecimal subtotal, BigDecimal shipping, BigDecimal discount) {
        return new Pricing(subtotal, shipping, discount, "KRW");
    }
}

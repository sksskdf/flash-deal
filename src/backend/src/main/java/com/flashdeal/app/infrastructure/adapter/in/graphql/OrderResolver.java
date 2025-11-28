package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.inventory.Quantity;
import com.flashdeal.app.domain.order.*;
import com.flashdeal.app.domain.product.ProductId;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class OrderResolver {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;

    public OrderResolver(
            CreateOrderUseCase createOrderUseCase,
            GetOrderUseCase getOrderUseCase,
            CancelOrderUseCase cancelOrderUseCase,
            CompletePaymentUseCase completePaymentUseCase) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.completePaymentUseCase = completePaymentUseCase;
    }

    @QueryMapping
    public Mono<Order> order(@Argument String id) {
        OrderId orderId = new OrderId(id);
        return getOrderUseCase.getOrder(orderId);
    }

    @QueryMapping
    public Flux<Order> ordersByUser(@Argument String userId) {
        UserId id = new UserId(userId);
        return getOrderUseCase.getOrdersByUserId(id);
    }

    @QueryMapping
    public Flux<Order> orders(@Argument OrderStatus status) {
        if (status != null) {
            return getOrderUseCase.getOrdersByStatus(status);
        }
        return Flux.empty();
    }

    @MutationMapping
    public Mono<Order> createOrder(@Argument CreateOrderInput input) {
        CreateOrderUseCase.CreateOrderCommand command = buildCreateOrderCommand(input);
        return createOrderUseCase.createOrder(command);
    }

    private CreateOrderUseCase.CreateOrderCommand buildCreateOrderCommand(CreateOrderInput input) {
        return new CreateOrderUseCase.CreateOrderCommand(
            new UserId(input.userId()),
            input.items().stream()
                .map(item -> new CreateOrderUseCase.OrderItemDto(
                    new ProductId(item.productId()),
                    item.quantity()
                ))
                .toList(),
            buildShippingDto(input.shipping()),
            input.idempotencyKey(),
            input.discount() != null ? input.discount() : BigDecimal.ZERO
        );
    }

    private CreateOrderUseCase.ShippingDto buildShippingDto(ShippingInput shipping) {
        return new CreateOrderUseCase.ShippingDto(
            shipping.recipient().name(),
            shipping.recipient().phone(),
            shipping.address().zipCode(),
            shipping.address().street(),
            shipping.address().city(),
            shipping.address().state(),
            shipping.address().country(),
            shipping.address().detailAddress()
        );
    }

    @MutationMapping
    public Mono<Order> cancelOrder(
            @Argument String id,
            @Argument String reason) {
        OrderId orderId = new OrderId(id);
        CancelOrderUseCase.CancelOrderCommand command = new CancelOrderUseCase.CancelOrderCommand(orderId, reason);
        return cancelOrderUseCase.cancelOrder(command);
    }

    @MutationMapping
    public Mono<Order> completePayment(
            @Argument String id,
            @Argument String transactionId) {
        OrderId orderId = new OrderId(id);
        CompletePaymentUseCase.CompletePaymentCommand command = 
            new CompletePaymentUseCase.CompletePaymentCommand(orderId, transactionId);
        return completePaymentUseCase.completePayment(command);
    }

    @SchemaMapping(typeName = "Order", field = "orderId")
    public String orderId(Order order) {
        return order.orderId().value();
    }

    @SchemaMapping(typeName = "Order", field = "userId")
    public String userId(Order order) {
        return order.userId().value();
    }

    @SchemaMapping(typeName = "Order", field = "orderNumber")
    public String orderNumber(Order order) {
        return order.getOrderNumber();
    }

    @SchemaMapping(typeName = "Order", field = "items")
    public List<OrderItem> items(Order order) {
        return order.items();
    }

    @SchemaMapping(typeName = "Order", field = "shipping")
    public Shipping shipping(Order order) {
        return order.shipping();
    }

    @SchemaMapping(typeName = "Order", field = "pricing")
    public Pricing pricing(Order order) {
        return order.pricing();
    }

    @SchemaMapping(typeName = "Order", field = "payment")
    public Payment payment(Order order) {
        return order.payment();
    }

    @SchemaMapping(typeName = "Order", field = "status")
    public OrderStatus status(Order order) {
        return order.status();
    }

    @SchemaMapping(typeName = "Order", field = "createdAt")
    public java.time.ZonedDateTime createdAt(Order order) {
        return order.createdAt().atZone(java.time.ZoneId.systemDefault());
    }

    @SchemaMapping(typeName = "OrderItem", field = "productId")
    public String productId(OrderItem orderItem) {
        return orderItem.productId().value();
    }

    @SchemaMapping(typeName = "OrderItem", field = "quantity")
    public int quantity(OrderItem orderItem) {
        return orderItem.quantity().value();
    }

    @SchemaMapping(typeName = "OrderItem", field = "snapshot")
    public Snapshot snapshot(OrderItem orderItem) {
        return orderItem.snapshot();
    }

    @SchemaMapping(typeName = "OrderItem", field = "subtotal")
    public BigDecimal subtotal(OrderItem orderItem) {
        return orderItem.getSubtotal();
    }

    @SchemaMapping(typeName = "OrderItem", field = "status")
    public OrderItemStatus status(OrderItem orderItem) {
        return orderItem.status();
    }

    @SchemaMapping(typeName = "Shipping", field = "method")
    public String method(Shipping shipping) {
        return shipping.method();
    }

    @SchemaMapping(typeName = "Shipping", field = "recipient")
    public Recipient recipient(Shipping shipping) {
        return shipping.recipient();
    }

    @SchemaMapping(typeName = "Shipping", field = "address")
    public Address address(Shipping shipping) {
        return shipping.address();
    }

    @SchemaMapping(typeName = "Shipping", field = "instructions")
    public String instructions(Shipping shipping) {
        return shipping.instructions();
    }

    @SchemaMapping(typeName = "Pricing", field = "subtotal")
    public BigDecimal subtotal(Pricing pricing) {
        return pricing.subtotal();
    }

    @SchemaMapping(typeName = "Pricing", field = "shipping")
    public BigDecimal shipping(Pricing pricing) {
        return pricing.shipping();
    }

    @SchemaMapping(typeName = "Pricing", field = "discount")
    public BigDecimal discount(Pricing pricing) {
        return pricing.discount();
    }

    @SchemaMapping(typeName = "Pricing", field = "total")
    public BigDecimal total(Pricing pricing) {
        return pricing.total();
    }

    @SchemaMapping(typeName = "Pricing", field = "currency")
    public String currency(Pricing pricing) {
        return pricing.currency();
    }

    @SchemaMapping(typeName = "Payment", field = "method")
    public String method(Payment payment) {
        return payment.method();
    }

    @SchemaMapping(typeName = "Payment", field = "status")
    public PaymentStatus status(Payment payment) {
        return payment.status();
    }

    @SchemaMapping(typeName = "Payment", field = "transactionId")
    public String transactionId(Payment payment) {
        return payment.transactionId();
    }

    @SchemaMapping(typeName = "Payment", field = "gateway")
    public String gateway(Payment payment) {
        return payment.gateway();
    }

    @SchemaMapping(typeName = "Snapshot", field = "title")
    public String title(Snapshot snapshot) {
        return snapshot.title();
    }

    @SchemaMapping(typeName = "Snapshot", field = "image")
    public String image(Snapshot snapshot) {
        return snapshot.image();
    }

    @SchemaMapping(typeName = "Snapshot", field = "price")
    public com.flashdeal.app.domain.product.Price price(Snapshot snapshot) {
        return snapshot.price();
    }

    @SchemaMapping(typeName = "Snapshot", field = "selectedOptions")
    public List<com.flashdeal.app.infrastructure.adapter.in.graphql.ProductResolver.SpecField> selectedOptions(Snapshot snapshot) {
        return snapshot.getSelectedOptions().entrySet().stream()
                .map(entry -> new com.flashdeal.app.infrastructure.adapter.in.graphql.ProductResolver.SpecField(
                        entry.getKey(), entry.getValue().toString()))
                .toList();
    }

    @SchemaMapping(typeName = "Recipient", field = "name")
    public String name(Recipient recipient) {
        return recipient.name();
    }

    @SchemaMapping(typeName = "Recipient", field = "phone")
    public String phone(Recipient recipient) {
        return recipient.phone();
    }

    @SchemaMapping(typeName = "Address", field = "street")
    public String street(Address address) {
        return address.street();
    }

    @SchemaMapping(typeName = "Address", field = "city")
    public String city(Address address) {
        return address.city();
    }

    @SchemaMapping(typeName = "Address", field = "state")
    public String state(Address address) {
        return null; // Domain Address에는 state 필드가 없음
    }

    @SchemaMapping(typeName = "Address", field = "zipCode")
    public String zipCode(Address address) {
        return address.zipCode();
    }

    @SchemaMapping(typeName = "Address", field = "country")
    public String country(Address address) {
        return address.country();
    }

    @SchemaMapping(typeName = "Address", field = "detailAddress")
    public String detailAddress(Address address) {
        return null; // Domain Address에는 detailAddress 필드가 없음
    }

    public record CreateOrderInput(
        String userId,
        List<OrderItemInput> items,
        ShippingInput shipping,
        String idempotencyKey,
        BigDecimal discount
    ) {}

    public record OrderItemInput(
        String productId,
        Quantity quantity
    ) {}

    public record ShippingInput(
        String method,
        RecipientInput recipient,
        AddressInput address,
        String instructions
    ) {}

    public record RecipientInput(
        String name,
        String phone
    ) {}

    public record AddressInput(
        String street,
        String city,
        String state,
        String zipCode,
        String country,
        String detailAddress
    ) {}
}


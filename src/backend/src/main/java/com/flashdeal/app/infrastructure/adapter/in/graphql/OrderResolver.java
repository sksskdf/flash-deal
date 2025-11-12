package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.order.*;
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
                    new com.flashdeal.app.domain.product.ProductId(item.productId()),
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

    @SchemaMapping(typeName = "OrderItem", field = "productId")
    public String productId(OrderItem orderItem) {
        return orderItem.productId().value();
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
        int quantity
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


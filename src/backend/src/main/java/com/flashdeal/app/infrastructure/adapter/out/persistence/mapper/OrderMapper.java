package com.flashdeal.app.infrastructure.adapter.out.persistence.mapper;

import com.flashdeal.app.domain.order.*;
import com.flashdeal.app.domain.product.Price;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.AddressDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.CancellationDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.OrderDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.OrderItemDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.OrderItemSnapshotDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.PaymentDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.PriceDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.PricingDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.RecipientDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.ShippingDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.UserInfoDocument;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Domain ↔ Document Mapper
 */
@Component
public class OrderMapper {

    /**
     * Domain Order → Document 변환
     */
    public OrderDocument toDocument(Order order) {
        List<OrderItemDocument> itemDocuments = order.getItems().stream()
                .map(this::toOrderItemDocument)
                .collect(Collectors.toList());

        return new OrderDocument(
                order.getOrderId().value(),
                order.getOrderNumber(),
                toUserInfoDocument(order.getUserId()),
                itemDocuments,
                toPricingDocument(order.getPricing()),
                toShippingDocument(order.getShipping()),
                toPaymentDocument(order.getPayment()),
                order.getStatus(),
                new ArrayList<>(), // statusHistory는 별도 서비스에서 처리
                new ArrayList<>(), // kafkaEvents는 별도 서비스에서 처리
                order.getIdempotencyKey(),
                toCancellationDocument(order.getCancellation()),
                null,
                order.getCreatedAt(),
                Instant.now()
        );
    }

    /**
     * Document → Domain Order 변환
     */
    public Order toDomain(OrderDocument document) {
        OrderId orderId = new OrderId(document.getId());
        UserId userId = new UserId(document.getUser().getId());

        List<OrderItem> items = document.getItems().stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList());

        Shipping shipping = toShipping(document.getShipping());
        Pricing pricing = toPricing(document.getPricing());
        Payment payment = toPayment(document.getPayment());
        OrderStatus status = document.getStatus() != null ? document.getStatus() : OrderStatus.PENDING;
        Cancellation cancellation = toCancellation(document.getCancellation());
        Instant createdAt = document.getCreatedAt() != null ? document.getCreatedAt() : Instant.now();

        Order order = new Order(
                orderId,
                userId,
                document.getIdempotencyKey(),
                items,
                shipping,
                pricing,
                payment,
                status,
                cancellation,
                createdAt
        );

        return order;
    }

    private OrderItemDocument toOrderItemDocument(OrderItem item) {
        return new OrderItemDocument(
                item.productId().value(),
                "flash",
                toOrderItemSnapshotDocument(item.snapshot()),
                item.quantity(),
                item.snapshot().price().sale(),
                item.snapshot().price().sale().multiply(BigDecimal.valueOf(item.quantity())),
                item.status(),
                null
        );
    }

    private OrderItemSnapshotDocument toOrderItemSnapshotDocument(Snapshot snapshot) {
        return new OrderItemSnapshotDocument(
                snapshot.title(),
                null,
                snapshot.image(),
                null,
                toPriceDocument(snapshot.price()),
                null,
                snapshot.getSelectedOptions()
        );
    }

    private OrderItem toOrderItem(OrderItemDocument itemDoc) {
        Price price = toPrice(itemDoc.getSnapshot().getPrice());
        Snapshot snapshot = new Snapshot(
                itemDoc.getSnapshot().getTitle(),
                itemDoc.getSnapshot().getImage(),
                price,
                itemDoc.getSnapshot().getSelectedOptions()
        );
        return new OrderItem(
                new ProductId(itemDoc.getProductId()),
                snapshot,
                itemDoc.getQuantity()
        );
    }

    private UserInfoDocument toUserInfoDocument(UserId userId) {
        return new UserInfoDocument(
            userId.value(),
            "user@example.com",
            "Test User",
            "010-1234-5678"
        );
    }

    private PricingDocument toPricingDocument(Pricing pricing) {
        return new PricingDocument(
                pricing.subtotal(),
                pricing.shipping(),
                pricing.discount(),
                pricing.total(),
                pricing.currency(),
                null
        );
    }

    private ShippingDocument toShippingDocument(Shipping shipping) {
        return new ShippingDocument(
                shipping.method(),
                toRecipientDocument(shipping.recipient()),
                toAddressDocument(shipping.address()),
                shipping.instructions()
        );
    }

    private RecipientDocument toRecipientDocument(Recipient recipient) {
        return new RecipientDocument(recipient.name(), recipient.phone());
    }

    private AddressDocument toAddressDocument(Address address) {
        return new AddressDocument(
                address.street(),
                address.city(),
                address.zipCode(),
                address.country()
        );
    }

    private PaymentDocument toPaymentDocument(Payment payment) {
        if (payment == null) return null;
        return new PaymentDocument(
                payment.method(),
                payment.status()
        );
    }

    private Pricing toPricing(PricingDocument document) {
        if (document == null) {
            return new Pricing(
                    java.math.BigDecimal.ZERO,
                    java.math.BigDecimal.ZERO,
                    java.math.BigDecimal.ZERO,
                    "KRW"
            );
        }
        return new Pricing(
                document.getSubtotal(),
                document.getShipping(),
                document.getDiscount(),
                document.getCurrency()
        );
    }

    private Payment toPayment(PaymentDocument document) {
        if (document == null) {
            return new Payment("CARD", PaymentStatus.PENDING, null, null);
        }
        return new Payment(
                document.getMethod(),
                document.getStatus(),
                null, // transactionId는 별도 필드에 저장될 수 있음
                null  // gateway는 별도 필드에 저장될 수 있음
        );
    }

    private Price toPrice(PriceDocument document) {
        if (document == null) {
            return null;
        }
        return new Price(document.getOriginal(), document.getSale(), document.getCurrency());
    }

    private PriceDocument toPriceDocument(Price price) {
        if (price == null) {
            return null;
        }
        return new PriceDocument(
            price.original(),
            price.sale(),
            price.currency(),
            price.discountRate()
        );
    }

    private Shipping toShipping(ShippingDocument document) {
        if (document == null) {
            return null;
        }
        Recipient recipient = new Recipient(
            document.getRecipient().getName(),
            document.getRecipient().getPhone()
        );
        Address address = new Address(
            document.getAddress().getStreet(),
            document.getAddress().getCity(),
            document.getAddress().getZipCode(),
            document.getAddress().getCountry()
        );
        return new Shipping(
            document.getMethod(),
            recipient,
            address,
            document.getInstructions()
        );
    }

    private CancellationDocument toCancellationDocument(Cancellation cancellation) {
        if (cancellation == null) {
            return null;
        }
        return new CancellationDocument(
                cancellation.isCancelled(),
                cancellation.reason(),
                cancellation.cancelledBy(),
                cancellation.cancelledAt(),
                cancellation.items()
        );
    }

    private Cancellation toCancellation(CancellationDocument document) {
        if (document == null) {
            return null;
        }
        return new Cancellation(
                document.getIsCancelled(),
                document.getReason(),
                document.getCancelledBy(),
                document.getCancelledAt(),
                document.getItems()
        );
    }
}
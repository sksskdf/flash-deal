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
                order.getOrderId().getValue(),
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

        Order order = new Order(
                orderId,
                userId,
                items,
                shipping,
                document.getIdempotencyKey(),
                document.getCreatedAt()
        );

        if (document.getStatus() != null) {
            order.transitionTo(document.getStatus());
        }

        if (document.getCancellation() != null) {
            order.setCancellation(toCancellation(document.getCancellation()));
        }

        return order;
    }

    private OrderItemDocument toOrderItemDocument(OrderItem item) {
        return new OrderItemDocument(
                item.getProductId().getValue(),
                "flash",
                toOrderItemSnapshotDocument(item.getSnapshot()),
                item.getQuantity(),
                item.getSnapshot().getPrice().getSale(),
                item.getSubtotal(),
                item.getStatus(),
                null
        );
    }

    private OrderItemSnapshotDocument toOrderItemSnapshotDocument(Snapshot snapshot) {
        return new OrderItemSnapshotDocument(
                snapshot.getTitle(),
                null,
                snapshot.getImage(),
                null,
                toPriceDocument(snapshot.getPrice()),
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
            userId.getValue(),
            "user@example.com",
            "Test User",
            "010-1234-5678"
        );
    }

    private PricingDocument toPricingDocument(Pricing pricing) {
        return new PricingDocument(
                pricing.getSubtotal(),
                pricing.getShipping(),
                pricing.getDiscount(),
                pricing.getTotal(),
                pricing.getCurrency(),
                null
        );
    }

    private ShippingDocument toShippingDocument(Shipping shipping) {
        return new ShippingDocument(
                shipping.getMethod(),
                toRecipientDocument(shipping.getRecipient()),
                toAddressDocument(shipping.getAddress()),
                shipping.getInstructions()
        );
    }

    private RecipientDocument toRecipientDocument(Recipient recipient) {
        return new RecipientDocument(recipient.getName(), recipient.getPhone());
    }

    private AddressDocument toAddressDocument(Address address) {
        return new AddressDocument(
                address.getStreet(),
                address.getCity(),
                address.getZipCode(),
                address.getCountry()
        );
    }

    private PaymentDocument toPaymentDocument(Payment payment) {
        if (payment == null) return null;
        return new PaymentDocument(
                payment.getMethod(),
                payment.getStatus()
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
            price.getOriginal(),
            price.getSale(),
            price.getCurrency(),
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
                cancellation.getIsCancelled(),
                cancellation.getReason(),
                cancellation.getCancelledBy(),
                cancellation.getCancelledAt(),
                cancellation.getItems()
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
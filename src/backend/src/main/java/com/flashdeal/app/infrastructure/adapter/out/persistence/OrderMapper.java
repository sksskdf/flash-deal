package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.order.*;
import com.flashdeal.app.domain.product.Price;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Order Domain ↔ Document Mapper
 */
@Component
public class OrderMapper {

    /**
     * Domain Order → Document 변환
     */
    public OrderDocument toDocument(Order order) {
        List<OrderItemDocument> itemDocuments = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            itemDocuments.add(toOrderItemDocument(item));
        }

        return new OrderDocument(
            order.getOrderId().getValue(),
            order.getOrderNumber(),
            toUserInfoDocument(order.getUserId()),
            itemDocuments,
            toPricingDocument(order.getPricing()),
            toShippingDocument(order.getShipping()),
            toPaymentDocument(order.getPayment()),
            order.getStatus(),
            new ArrayList<>(),
            new ArrayList<>(),
            order.getIdempotencyKey(),
            null,
            null,
            Instant.now(),
            Instant.now()
        );
    }

    /**
     * Document → Domain Order 변환
     */
    public Order toDomain(OrderDocument document) {
        OrderId orderId = new OrderId(document.getId());
        UserId userId = new UserId(document.getUser().getId());
        
        // 문서의 아이템 -> 도메인 아이템 변환
        List<OrderItem> items = new ArrayList<>();
        if (document.getItems() != null) {
            for (OrderItemDocument itemDoc : document.getItems()) {
                items.add(toOrderItem(itemDoc));
            }
        }

        // 배송 정보 매핑
        Shipping shipping = toShipping(document.getShipping());

        Order order = new Order(
            orderId,
            userId,
            items,
            shipping
        );

        // 상태 설정
        if (document.getStatus() != null) {
            order.transitionTo(document.getStatus());
        }

        return order;
    }

    private OrderItemDocument toOrderItemDocument(OrderItem item) {
        OrderItemSnapshotDocument snapshot = new OrderItemSnapshotDocument(
            item.getSnapshot().getTitle(),
            null, // subtitle 미사용
            item.getSnapshot().getImage(),
            null, // category 미사용
            new PriceDocument(
                item.getSnapshot().getPrice().getOriginal(),
                item.getSnapshot().getPrice().getSale(),
                item.getSnapshot().getPrice().getCurrency(),
                null // rate 계산 미사용
            ),
            null, // specs 미사용
            item.getSnapshot().getSelectedOptions()
        );

        return new OrderItemDocument(
            item.getProductId().getValue(),
            null, // dealType 미사용
            snapshot,
            item.getQuantity(),
            item.getSnapshot().getPrice().getSale(),
            item.getSubtotal(),
            null, // status 미사용
            null // tracking 미사용
        );
    }

    private OrderItem toOrderItem(OrderItemDocument itemDoc) {
        // Price
        Price price = new Price(
            itemDoc.getSnapshot().getPrice().getOriginal(),
            itemDoc.getSnapshot().getPrice().getSale(),
            itemDoc.getSnapshot().getPrice().getCurrency()
        );

        // Snapshot (필수 필드 보정)
        String title = itemDoc.getSnapshot().getTitle();
        if (title == null || title.isBlank()) {
            title = "Item";
        }
        String image = itemDoc.getSnapshot().getImage();
        if (image == null) {
            image = "";
        }
        Map<String, Object> selectedOptions = itemDoc.getSnapshot().getSelectedOptions();
        if (selectedOptions == null) {
            selectedOptions = java.util.Collections.emptyMap();
        }

        Snapshot snapshot = new Snapshot(title, image, price, selectedOptions);

        return new OrderItem(new com.flashdeal.app.domain.product.ProductId(itemDoc.getProductId()), snapshot, itemDoc.getQuantity());
    }

    private Shipping toShipping(ShippingDocument shippingDoc) {
        if (shippingDoc == null) {
            return new Shipping(
                "STANDARD",
                new Recipient("Default User", "010-0000-0000"),
                new Address("Default Street", "Default City", "00000", "KR"),
                "Default instructions"
            );
        }

        Recipient recipient = new Recipient(
            shippingDoc.getRecipient().getName(),
            shippingDoc.getRecipient().getPhone()
        );

        AddressDocument addr = shippingDoc.getAddress();
        Address address = new Address(
            addr.getStreet(),
            addr.getCity(),
            addr.getZipCode(),
            addr.getCountry()
        );

        return new Shipping(
            shippingDoc.getMethod() == null ? "STANDARD" : shippingDoc.getMethod(),
            recipient,
            address,
            shippingDoc.getInstructions()
        );
    }

    private UserInfoDocument toUserInfoDocument(UserId userId) {
        return new UserInfoDocument(
            userId.getValue(),
            "user@example.com", // 실제로는 User 서비스에서 조회
            "User Name",
            "010-1234-5678"
        );
    }

    private PricingDocument toPricingDocument(Pricing pricing) {
        return new PricingDocument(
            pricing.getSubtotal(),
            pricing.getShipping(),
            pricing.getTax(),
            pricing.getDiscount(),
            pricing.getTotal(),
            pricing.getCurrency(),
            null // breakdown은 별도 처리
        );
    }

    private ShippingDocument toShippingDocument(Shipping shipping) {
        return new ShippingDocument(
            shipping.getMethod(),
            toRecipientDocument(shipping.getRecipient()),
            toAddressDocument(shipping.getAddress()),
            shipping.getInstructions(),
            shipping.getPreferredTime()
        );
    }

    private RecipientDocument toRecipientDocument(Recipient recipient) {
        return new RecipientDocument(
            recipient.getName(),
            recipient.getPhone()
        );
    }

    private AddressDocument toAddressDocument(Address address) {
        return new AddressDocument(
            address.getType(),
            address.getStreet(),
            address.getStreet2(),
            address.getCity(),
            address.getState(),
            address.getZipCode(),
            address.getCountry(),
            toCoordinatesDocument(address.getCoordinates())
        );
    }

    private CoordinatesDocument toCoordinatesDocument(Address.Coordinates coordinates) {
        return new CoordinatesDocument(
            coordinates.getLat(),
            coordinates.getLng()
        );
    }

    private PaymentDocument toPaymentDocument(Payment payment) {
        return new PaymentDocument(
            payment.getMethod(),
            toCardDocument(payment.getCard()),
            payment.getStatus(),
            toPaymentGatewayDocument(payment.getGatewayInfo()),
            toRefundDocument(payment.getRefund()),
            toInstallmentDocument(payment.getInstallment())
        );
    }

    private CardDocument toCardDocument(Payment.Card card) {
        return new CardDocument(
            card.getLast4(),
            card.getBrand(),
            card.getExpiryMonth(),
            card.getExpiryYear()
        );
    }

    private PaymentGatewayDocument toPaymentGatewayDocument(Payment.Gateway gateway) {
        return new PaymentGatewayDocument(
            gateway.getProvider(),
            gateway.getTransactionId(),
            gateway.getReceiptUrl(),
                gateway.getChargedAt().toInstant()
        );
    }

    private RefundDocument toRefundDocument(Payment.Refund refund) {
        if (refund == null) return null;
        return new RefundDocument(
            refund.getId(),
            refund.getAmount(),
            refund.getReason(),
            refund.getStatus(),
                refund.getRequestedAt().toInstant(),
                refund.getProcessedAt().toInstant()
        );
    }

    private InstallmentDocument toInstallmentDocument(Payment.Installment installment) {
        if (installment == null) return null;
        return new InstallmentDocument(
            installment.getCount(),
            installment.getAmount(),
                installment.getFirstDue().toInstant(),
                installment.getLastDue().toInstant()
        );
    }
}

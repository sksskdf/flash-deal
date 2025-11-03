package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.order.*;
import com.flashdeal.app.domain.product.Price;
import com.flashdeal.app.domain.product.ProductId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
                null, // cancellation은 별도 처리
                null, // metadata는 현재 미사용
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

        // 상태, 결제 등 추가 정보 복원
        if (document.getStatus() != null) {
            order.transitionTo(document.getStatus());
        }
        // pricing, payment 등은 Order 생성자에서 초기화되므로, 필요 시 document 값으로 덮어쓰기

        return order;
    }

    private OrderItemDocument toOrderItemDocument(OrderItem item) {
        return new OrderItemDocument(
                item.getProductId().getValue(),
                "flash", // dealType은 현재 도메인에 없으므로 하드코딩
                toOrderItemSnapshotDocument(item.getSnapshot()),
                item.getQuantity(),
                item.getSnapshot().getPrice().getSale(),
                item.getSubtotal(),
                item.getStatus(),
                null // tracking은 현재 미사용
        );
    }

    private OrderItemSnapshotDocument toOrderItemSnapshotDocument(Snapshot snapshot) {
        return new OrderItemSnapshotDocument(
                snapshot.getTitle(),
                null, // subtitle은 현재 미사용
                snapshot.getImage(),
                null, // category는 현재 미사용
                toPriceDocument(snapshot.getPrice()),
                null, // specs는 현재 미사용
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
        // 실제 구현에서는 User 서비스를 통해 사용자 정보를 조회해야 함
        return new UserInfoDocument(userId.getValue(), "user@example.com", "Test User", "010-1234-5678");
    }

    private PricingDocument toPricingDocument(Pricing pricing) {
        return new PricingDocument(
                pricing.getSubtotal(),
                pricing.getShipping(),
                pricing.getTax(),
                pricing.getDiscount(),
                pricing.getTotal(),
                pricing.getCurrency(),
                null // breakdown은 현재 미사용
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
        return new RecipientDocument(recipient.getName(), recipient.getPhone());
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
        if (coordinates == null) return null;
        return new CoordinatesDocument(coordinates.getLat(), coordinates.getLng());
    }

    private PaymentDocument toPaymentDocument(Payment payment) {
        if (payment == null) return null;
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
        if (card == null) return null;
        return new CardDocument(card.getLast4(), card.getBrand(), card.getExpiryMonth(), card.getExpiryYear());
    }

    private PaymentGatewayDocument toPaymentGatewayDocument(Payment.Gateway gateway) {
        if (gateway == null) return null;
        return new PaymentGatewayDocument(
                gateway.getProvider(),
                gateway.getTransactionId(),
                gateway.getReceiptUrl(),
                gateway.getChargedAt() != null ? gateway.getChargedAt().toInstant() : null
        );
    }

    private RefundDocument toRefundDocument(Payment.Refund refund) {
        if (refund == null) return null;
        return new RefundDocument(
                refund.getId(),
                refund.getAmount(),
                refund.getReason(),
                refund.getStatus(),
                refund.getRequestedAt() != null ? refund.getRequestedAt().toInstant() : null,
                refund.getProcessedAt() != null ? refund.getProcessedAt().toInstant() : null
        );
    }

    private InstallmentDocument toInstallmentDocument(Payment.Installment installment) {
        if (installment == null) return null;
        return new InstallmentDocument(
                installment.getCount(),
                installment.getAmount(),
                installment.getFirstDue() != null ? installment.getFirstDue().toInstant() : null,
                installment.getLastDue() != null ? installment.getLastDue().toInstant() : null
        );
    }

    private Payment toPayment(PaymentDocument document) {
        if (document == null) return null;
        return new Payment(document.getMethod(), document.getStatus(), document.getGateway() != null ? document.getGateway().getTransactionId() : null, document.getGateway() != null ? document.getGateway().getProvider() : null);
    }

    private Price toPrice(PriceDocument document) {
        if (document == null) return null;
        return new Price(document.getOriginal(), document.getSale(), document.getCurrency());
    }

    private PriceDocument toPriceDocument(Price price) {
        if (price == null) return null;
        return new PriceDocument(
                price.getOriginal(),
                price.getSale(),
                price.getCurrency(),
                price.discountRate()
        );
    }

    private Pricing toPricing(PricingDocument document) {
        if (document == null) return null;
        return new Pricing(document.getSubtotal(), document.getShipping(), document.getDiscount(), document.getCurrency());
    }

    private Shipping toShipping(ShippingDocument document) {
        if (document == null) return null;
        return new Shipping(
                document.getMethod(),
                new Recipient(document.getRecipient().getName(), document.getRecipient().getPhone()),
                new Address(
                        document.getAddress().getStreet(),
                        document.getAddress().getCity(),
                        document.getAddress().getZipCode(),
                        document.getAddress().getCountry()
                ),
                document.getInstructions()
        );
    }
}
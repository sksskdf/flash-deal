package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.order.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Domain ↔ Document Mapper
 */
@Component
public class OrderMapper {

    /**
     * Domain Order → Document 변환
     */
    public OrderDocument toDocument(Order order) {
        return new OrderDocument(
            order.getOrderId().getValue(),
            order.getOrderNumber(),
            toUserInfoDocument(order.getUserId()),
            new ArrayList<>(), // items는 별도 처리
            toPricingDocument(order.getPricing()),
            toShippingDocument(order.getShipping()),
            toPaymentDocument(order.getPayment()),
            order.getStatus(),
            new ArrayList<>(), // statusHistory는 별도 처리
            new ArrayList<>(), // kafkaEvents는 별도 처리
            order.getIdempotencyKey(),
            null, // cancellation은 별도 처리
            null, // metadata는 별도 처리
            ZonedDateTime.now(), // createdAt
            ZonedDateTime.now()  // updatedAt
        );
    }

    /**
     * Document → Domain Order 변환
     */
    public Order toDomain(OrderDocument document) {
        OrderId orderId = new OrderId(document.getId());
        UserId userId = new UserId(document.getUser().getId());
        
        // 기본 Order 생성 (실제로는 더 복잡한 로직 필요)
        // 임시로 빈 리스트와 기본 Shipping으로 생성
        List<OrderItem> items = new ArrayList<>();
        Shipping shipping = new Shipping("STANDARD", 
            new Recipient("Default User", "010-0000-0000"),
            new Address("Default Street", "Default City", "00000", "KR"),
            "Default instructions");
        
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
            gateway.getChargedAt()
        );
    }

    private RefundDocument toRefundDocument(Payment.Refund refund) {
        if (refund == null) return null;
        return new RefundDocument(
            refund.getId(),
            refund.getAmount(),
            refund.getReason(),
            refund.getStatus(),
            refund.getRequestedAt(),
            refund.getProcessedAt()
        );
    }

    private InstallmentDocument toInstallmentDocument(Payment.Installment installment) {
        if (installment == null) return null;
        return new InstallmentDocument(
            installment.getCount(),
            installment.getAmount(),
            installment.getFirstDue(),
            installment.getLastDue()
        );
    }
}

package com.flashdeal.app.infrastructure.adapter.out.messaging;

import com.flashdeal.app.application.port.in.CancelOrderUseCase;
import com.flashdeal.app.application.port.in.CompletePaymentUseCase;
import com.flashdeal.app.application.port.in.ConfirmInventoryUseCase;
import com.flashdeal.app.application.port.in.GetOrderUseCase;
import com.flashdeal.app.application.port.in.ReleaseInventoryUseCase;
import com.flashdeal.app.application.port.in.ReserveInventoryUseCase;
import com.flashdeal.app.domain.order.OrderId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class KafkaEventConsumer {

    private final GetOrderUseCase getOrderUseCase;
    private final ReserveInventoryUseCase reserveInventoryUseCase;
    private final CompletePaymentUseCase completePaymentUseCase;
    private final ConfirmInventoryUseCase confirmInventoryUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final ReleaseInventoryUseCase releaseInventoryUseCase;

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventConsumer.class);

    public KafkaEventConsumer(GetOrderUseCase getOrderUseCase, 
                              ReserveInventoryUseCase reserveInventoryUseCase, 
                              CompletePaymentUseCase completePaymentUseCase, 
                              ConfirmInventoryUseCase confirmInventoryUseCase,
                              CancelOrderUseCase cancelOrderUseCase,
                              ReleaseInventoryUseCase releaseInventoryUseCase) {
        this.getOrderUseCase = getOrderUseCase;
        this.reserveInventoryUseCase = reserveInventoryUseCase;
        this.completePaymentUseCase = completePaymentUseCase;
        this.confirmInventoryUseCase = confirmInventoryUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.releaseInventoryUseCase = releaseInventoryUseCase;
    }

    /**
     * 주문 생성 이벤트 수신
     */
    @KafkaListener(topics = "order.created", groupId = "flashdeal-group")
    public void handleOrderCreated(@Payload Map<String, Object> event,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset,
                                 Acknowledgment acknowledgment) {

        logger.info("Received OrderCreated event: {}", event);

        try {
            // 이벤트 처리 로직
            String orderIdStr = (String) event.get("orderId");
            OrderId orderId = new OrderId(orderIdStr);

            getOrderUseCase.getOrder(orderId)
                    .flatMapMany(order -> Flux.fromIterable(order.items()))
                    .flatMap(orderItem -> {
                        ReserveInventoryUseCase.ReserveInventoryCommand command = new ReserveInventoryUseCase.ReserveInventoryCommand(orderItem.productId(), orderItem.quantity());
                        return reserveInventoryUseCase.reserve(command);
                    })
                    .doOnComplete(() -> {
                        acknowledgment.acknowledge();
                        logger.info("Successfully processed OrderCreated event for orderId: {}", orderIdStr);
                    })
                    .doOnError(error -> {
                        logger.error("Error processing OrderCreated event: {}", event, error);
                        // 에러 처리 로직 (예: DLQ 전송)
                    })
                    .subscribe();

        } catch (Exception e) {
            logger.error("Error processing OrderCreated event: {}", event, e);
            // 에러 처리 로직
            // - 재시도
            // - DLQ 전송 등
        }
    }

    /**
     * 결제 완료 이벤트 수신
     */
    @KafkaListener(topics = "payment.completed", groupId = "flashdeal-group")
    public void handlePaymentCompleted(@Payload Map<String, Object> event,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset,
                                     Acknowledgment acknowledgment) {

        logger.info("Received PaymentCompleted event: {}", event);

        try {
            String orderIdStr = (String) event.get("orderId");
            String transactionId = (String) event.get("transactionId");
            OrderId orderId = new OrderId(orderIdStr);

            CompletePaymentUseCase.CompletePaymentCommand command = new CompletePaymentUseCase.CompletePaymentCommand(orderId, transactionId);
            completePaymentUseCase.completePayment(command)
                    .flatMap(order -> Flux.fromIterable(order.items())
                            .flatMap(orderItem -> {
                                ConfirmInventoryUseCase.ConfirmInventoryCommand confirmCommand = new ConfirmInventoryUseCase.ConfirmInventoryCommand(orderItem.productId(), orderItem.quantity());
                                return confirmInventoryUseCase.confirm(confirmCommand);
                            })
                            .then(Mono.just(order)))
                    .doOnSuccess(order -> {
                        acknowledgment.acknowledge();
                        logger.info("Successfully processed PaymentCompleted event for orderId: {}",
                                order.orderId().value());
                    })
                    .doOnError(error -> {
                        logger.error("Error processing PaymentCompleted event: {}", event, error);
                    })
                    .subscribe();

        } catch (Exception e) {
            logger.error("Error processing PaymentCompleted event: {}", event, e);
        }
    }

    /**
     * 재고 예약 이벤트 수신
     */
    @KafkaListener(topics = "inventory.reserved", groupId = "flashdeal-group")
    public void handleInventoryReserved(@Payload Map<String, Object> event,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset,
                                     Acknowledgment acknowledgment) {

        logger.info("Received InventoryReserved event: {}", event);

        try {
            String productId = (String) event.get("productId");
            logger.info("Inventory reserved for productId: {}", productId);

            acknowledgment.acknowledge();
            logger.info("Successfully processed InventoryReserved event for productId: {}", productId);

        } catch (Exception e) {
            logger.error("Error processing InventoryReserved event: {}", event, e);
        }
    }

    /**
     * 주문 취소 이벤트 수신
     */
    @KafkaListener(topics = "order.cancelled", groupId = "flashdeal-group")
    public void handleOrderCancelled(@Payload Map<String, Object> event,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset,
                                   Acknowledgment acknowledgment) {

        logger.info("Received OrderCancelled event: {}", event);

        try {
            String orderIdStr = (String) event.get("orderId");
            String reason = (String) event.get("reason");
            OrderId orderId = new OrderId(orderIdStr);

            CancelOrderUseCase.CancelOrderCommand command = new CancelOrderUseCase.CancelOrderCommand(orderId, reason);
            cancelOrderUseCase.cancelOrder(command)
                    .flatMap(order -> Flux.fromIterable(order.items())
                            .flatMap(orderItem -> {
                                ReleaseInventoryUseCase.ReleaseInventoryCommand releaseCommand = new ReleaseInventoryUseCase.ReleaseInventoryCommand(orderItem.productId(), orderItem.quantity());
                                return releaseInventoryUseCase.release(releaseCommand);
                            })
                            .then(Mono.just(order)))
                    .doOnSuccess(order -> {
                        acknowledgment.acknowledge();
                        logger.info("Successfully processed OrderCancelled event for orderId: {}",
                                order.orderId().value());
                    })
                    .doOnError(error -> {
                        logger.error("Error processing OrderCancelled event: {}", event, error);
                        // 에러 처리 로직 (예: DLQ 전송)
                    })
                    .subscribe();

        } catch (Exception e) {
            logger.error("Error processing OrderCancelled event: {}", event, e);
        }
    }
}






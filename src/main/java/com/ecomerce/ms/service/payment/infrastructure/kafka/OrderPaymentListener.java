package com.ecomerce.ms.service.payment.infrastructure.kafka;

import com.ecomerce.ms.service.OrderMessage;
import com.ecomerce.ms.service.OrderingSagaKey;
import com.ecomerce.ms.service.PaymentProcessingCommand;
import com.ecomerce.ms.service.PaymentProcessingReply;
import com.ecomerce.ms.service.SagaStepStatusMessage;
import com.ecomerce.ms.service.payment.application.command.CompensateOrderPaymentCommand;
import com.ecomerce.ms.service.payment.application.command.CreateOrderPaymentCommand;
import com.ecomerce.ms.service.payment.domain.shared.external.order.Order;
import com.ecomerce.ms.service.payment.domain.shared.external.order.OrderItem;
import com.huyle.ms.command.CommandGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.stream.Collectors;

import static com.ecomerce.ms.service.payment.domain.shared.Constants.PAYMENT_PROCESSING_COMPENSATION_TOPIC;
import static com.ecomerce.ms.service.payment.domain.shared.Constants.PAYMENT_PROCESSING_REPLY_TOPIC;
import static com.ecomerce.ms.service.payment.domain.shared.Constants.PAYMENT_PROCESSING_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentListener {
    private final CommandGateway commandGateway;
    private final KafkaTemplate<OrderingSagaKey, PaymentProcessingReply> paymentProcessingTemplate;

    @KafkaListener(topics = PAYMENT_PROCESSING_TOPIC)
    public void onPaymentProcessingCommand(@Payload PaymentProcessingCommand paymentProcessingCommand,
                                            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) OrderingSagaKey sagaKey) {
        processPaymentEvent(paymentProcessingCommand, sagaKey, false);
    }

    @KafkaListener(topics = PAYMENT_PROCESSING_COMPENSATION_TOPIC)
    public void onPaymentProcessingCompensationCommand(@Payload PaymentProcessingCommand paymentProcessingCommand,
                                           @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) OrderingSagaKey sagaKey) {
        processPaymentEvent(paymentProcessingCommand, sagaKey, true);
    }

    private void processPaymentEvent(PaymentProcessingCommand paymentProcessingCommand, OrderingSagaKey sagaKey, boolean isCompensation) {
        OrderMessage orderMessage = paymentProcessingCommand.getOrder();
        List<OrderItem> orderItems = orderMessage.getOrderItems().stream()
                .map(item -> OrderItem.builder()
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .merchantUserId(item.getMerchantUserId())
                        .build())
                .collect(Collectors.toList());
        Order order = Order.builder()
                .id(orderMessage.getOrderId())
                .customerUserId(orderMessage.getCustomerId())
                .orderItems(orderItems)
                .build();

        SagaStepStatusMessage sagaStepStatusMessage;
        try {
            if (isCompensation) {
                commandGateway.handle(CompensateOrderPaymentCommand.builder()
                        .order(order)
                        .build());
                sagaStepStatusMessage = SagaStepStatusMessage.COMPENSATED;
            } else {
                commandGateway.handle(CreateOrderPaymentCommand.builder()
                        .order(order)
                        .build());
                sagaStepStatusMessage = SagaStepStatusMessage.SUCCEEDED;
            }
        } catch (RuntimeException e) {
            sagaStepStatusMessage = isCompensation ? SagaStepStatusMessage.COMPENSATION_FAILED : SagaStepStatusMessage.FAILED;
        }
        var reply = PaymentProcessingReply.newBuilder()
                .setSagaMetadata(paymentProcessingCommand.getSagaMetadata())
                .setSagaStepStatus(sagaStepStatusMessage)
                .build();
        paymentProcessingTemplate.send(PAYMENT_PROCESSING_REPLY_TOPIC, sagaKey, reply)
                .addCallback(new ListenableFutureCallback<SendResult<OrderingSagaKey, PaymentProcessingReply>>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        log.info("Unable to send message=[\"{}\"] due to : {}", reply, ex.getMessage());
                    }

                    @Override
                    public void onSuccess(SendResult<OrderingSagaKey, PaymentProcessingReply> result) {
                        log.info("Sent message=[\"{}\"] with offset=[\"{}\"]", reply, result.getRecordMetadata().offset());
                    }
                });
    }
}

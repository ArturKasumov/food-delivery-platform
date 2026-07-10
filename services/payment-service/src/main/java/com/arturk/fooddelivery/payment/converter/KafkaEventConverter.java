package com.arturk.fooddelivery.payment.converter;

import com.arturk.fooddelivery.contracts.avro.common.v1.EventMetadata;
import com.arturk.fooddelivery.contracts.avro.payment.v1.PaymentCompletedEvent;
import com.arturk.fooddelivery.contracts.avro.payment.v1.PaymentFailedEvent;
import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;
import com.arturk.fooddelivery.payment.dto.PaymentCompletedEventPayload;
import com.arturk.fooddelivery.payment.dto.PaymentFailedEventPayload;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConverter {

    public PaymentCompletedEvent toPaymentCompletedEvent(OutboxEventEntity outboxEvent,
                                                         PaymentCompletedEventPayload payload)
    {
        return PaymentCompletedEvent.newBuilder()
                .setMetadata(toMetadata(outboxEvent))
                .setPayload(com.arturk.fooddelivery.contracts.avro.payment.v1.PaymentCompletedPayload.newBuilder()
                        .setPaymentId(payload.paymentId().toString())
                        .setOrderId(payload.orderId().toString())
                        .setCustomerId(payload.customerId().toString())
                        .setAmount(payload.amount().toPlainString())
                        .setStatus(payload.status().name())
                        .build())
                .build();
    }

    public PaymentFailedEvent toPaymentFailedEvent(OutboxEventEntity outboxEvent,
                                                   PaymentFailedEventPayload payload)
    {
        return PaymentFailedEvent.newBuilder()
                .setMetadata(toMetadata(outboxEvent))
                .setPayload(com.arturk.fooddelivery.contracts.avro.payment.v1.PaymentFailedPayload.newBuilder()
                        .setPaymentId(payload.paymentId().toString())
                        .setOrderId(payload.orderId().toString())
                        .setCustomerId(payload.customerId().toString())
                        .setAmount(payload.amount().toPlainString())
                        .setStatus(payload.status().name())
                        .setFailureReason(payload.failureReason())
                        .build())
                .build();
    }

    private EventMetadata toMetadata(OutboxEventEntity outboxEvent) {
        return EventMetadata.newBuilder()
                .setEventId(outboxEvent.getId().toString())
                .setEventType(outboxEvent.getEventType())
                .setAggregateType(outboxEvent.getAggregateType())
                .setAggregateId(outboxEvent.getAggregateId().toString())
                .setCorrelationId(outboxEvent.getCorrelationId())
                .setOccurredAt(outboxEvent.getCreatedAt().toString())
                .build();
    }
}

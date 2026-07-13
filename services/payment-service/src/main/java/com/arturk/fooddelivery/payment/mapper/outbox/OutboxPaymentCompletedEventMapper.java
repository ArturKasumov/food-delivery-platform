package com.arturk.fooddelivery.payment.mapper.outbox;

import com.arturk.fooddelivery.payment.config.properties.KafkaTopicsProperties;
import com.arturk.fooddelivery.payment.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.payment.constants.PaymentEventTypes;
import com.arturk.fooddelivery.payment.converter.JsonConverter;
import com.arturk.fooddelivery.payment.converter.OutboxEventConverter;
import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;
import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.dto.payload.PaymentCompletedEventPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxPaymentCompletedEventMapper implements OutboxEventMapper<PaymentEntity> {

    private final OutboxEventConverter outboxEventConverter;
    private final JsonConverter jsonConverter;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Override
    public String getEventType() {
        return PaymentEventTypes.PAYMENT_COMPLETED_EVENT_TYPE;
    }

    @Override
    public Class<PaymentEntity> supportsEntityType() {
        return PaymentEntity.class;
    }

    @Override
    public OutboxEventEntity mapToOutboxEvent(PaymentEntity entity) {
        PaymentCompletedEventPayload orderCreatedEventPayload =
                outboxEventConverter.toPaymentCompletedEventPayload(entity);

        return new OutboxEventEntity(
                UUID.randomUUID(),
                PaymentEventTypes.PAYMENT_AGGREGATE_TYPE,
                entity.getId(),
                PaymentEventTypes.PAYMENT_COMPLETED_EVENT_TYPE,
                MDC.get(CorrelationIdConstants.MDC_KEY),
                kafkaTopicsProperties.paymentEvents(),
                jsonConverter.toJson(orderCreatedEventPayload));
    }
}

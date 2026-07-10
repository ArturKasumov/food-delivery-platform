package com.arturk.fooddelivery.payment.mapper.kafka;

import com.arturk.fooddelivery.payment.constants.PaymentEventTypes;
import com.arturk.fooddelivery.payment.converter.JsonConverter;
import com.arturk.fooddelivery.payment.converter.KafkaEventConverter;
import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;
import com.arturk.fooddelivery.payment.dto.PaymentCompletedEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPaymentCompletedEventMapper implements KafkaEventMapper {

    private final JsonConverter jsonConverter;
    private final KafkaEventConverter kafkaEventConverter;

    @Override
    public String getEventType() {
        return PaymentEventTypes.PAYMENT_COMPLETED_EVENT_TYPE;
    }

    @Override
    public Object mapToKafkaEvent(OutboxEventEntity event) {
        PaymentCompletedEventPayload payload = jsonConverter.fromJson(
                event.getPayload(),
                new TypeReference<>() {
                }
        );
        return kafkaEventConverter.toPaymentCompletedEvent(event, payload);
    }
}

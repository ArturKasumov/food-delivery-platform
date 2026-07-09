package com.arturk.fooddelivery.order.mapper.kafka;

import com.arturk.fooddelivery.order.constants.OrderEventTypes;
import com.arturk.fooddelivery.order.converter.JsonConverter;
import com.arturk.fooddelivery.order.converter.KafkaEventConverter;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.messaging.outbox.OrderCreatedEventPayload;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOrderCreatedEventMapper implements KafkaEventMapper {

    private final JsonConverter jsonConverter;
    private final KafkaEventConverter orderCreatedEventConverter;

    @Override
    public String getEventType() {
        return OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
    }

    @Override
    public Object mapToKafkaEvent(OutboxEventEntity event) {
        OrderCreatedEventPayload payload = jsonConverter.fromJson(
                event.getPayload(),
                new TypeReference<>() {
                }
        );
        return orderCreatedEventConverter.toKafkaEvent(event, payload);
    }
}

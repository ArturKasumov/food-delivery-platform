package com.arturk.fooddelivery.order.mapper.outbox;

import com.arturk.fooddelivery.order.config.properties.KafkaTopicsProperties;
import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.order.constants.OrderEventTypes;
import com.arturk.fooddelivery.order.converter.JsonConverter;
import com.arturk.fooddelivery.order.converter.OutboxEventsConverter;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.dto.OrderCreatedEventPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;

@Component
@RequiredArgsConstructor
public class OutboxOrderCreatedEventMapper implements OutboxEventMapper<CustomerOrderEntity> {

    private final OutboxEventsConverter outboxEventsConverter;
    private final JsonConverter jsonConverter;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Override
    public String getEventType() {
        return OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
    }

    @Override
    public Class<CustomerOrderEntity> supportsEntityType() {
        return CustomerOrderEntity.class;
    }

    @Override
    public OutboxEventEntity mapToOutboxEvent(CustomerOrderEntity entity) {
        UUID eventId = UUID.randomUUID();

        OrderCreatedEventPayload orderCreatedEventPayload =
                outboxEventsConverter.toOrderCreatedEventOutboxPayload(entity);

        return new OutboxEventEntity(
                eventId,
                ORDER_AGGREGATE_TYPE,
                entity.getId(),
                ORDER_CREATED_EVENT_TYPE,
                MDC.get(CorrelationIdConstants.MDC_KEY),
                kafkaTopicsProperties.orderEvents(),
                jsonConverter.toJson(orderCreatedEventPayload));
    }
}

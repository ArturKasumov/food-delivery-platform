package com.arturk.fooddelivery.order.service;

import com.arturk.fooddelivery.order.config.KafkaTopicsProperties;
import com.arturk.fooddelivery.order.converter.JsonConverter;
import com.arturk.fooddelivery.order.converter.OutboxEventsConverter;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.dto.outbox.OrderCreatedEventOutboxPayload;
import com.arturk.fooddelivery.order.dto.outbox.OutboxEventEnvelope;
import com.arturk.fooddelivery.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;

@Service
@RequiredArgsConstructor
public class OrderOutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventsConverter outboxEventsConverter;
    private final JsonConverter jsonConverter;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    @Transactional
    public void saveOrderCreatedEvent(CustomerOrderEntity order) {
        UUID eventId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        OrderCreatedEventOutboxPayload orderCreatedEventPayload =
                outboxEventsConverter.toOrderCreatedEventOutboxPayload(order);

        OutboxEventEnvelope<OrderCreatedEventOutboxPayload> outboxEventEnvelope = createOutboxEventEnvelope(
                eventId,
                ORDER_CREATED_EVENT_TYPE,
                ORDER_AGGREGATE_TYPE,
                order.getId(),
                occurredAt,
                orderCreatedEventPayload
        );


        OutboxEventEntity outboxEvent = new OutboxEventEntity(
                eventId,
                ORDER_AGGREGATE_TYPE,
                order.getId(),
                ORDER_CREATED_EVENT_TYPE,
                kafkaTopicsProperties.orderEvents(),
                jsonConverter.toJson(outboxEventEnvelope));

        outboxEventRepository.save(outboxEvent);
    }

    private <T> OutboxEventEnvelope<T> createOutboxEventEnvelope(UUID eventId,
                                                                 String eventType,
                                                                 String aggregateType,
                                                                 UUID aggregateId,
                                                                 Instant occurredAt,
                                                                 T payload) {
        return new OutboxEventEnvelope<>(
                eventId,
                eventType,
                aggregateType,
                aggregateId,
                occurredAt.toString(),
                payload
        );
    }
}

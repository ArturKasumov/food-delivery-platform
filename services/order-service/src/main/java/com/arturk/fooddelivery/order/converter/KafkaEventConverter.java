package com.arturk.fooddelivery.order.converter;

import com.arturk.fooddelivery.contracts.avro.common.v1.EventMetadata;
import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEventPayload;
import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedItem;
import com.arturk.fooddelivery.order.dto.outbox.OrderCreatedEventOutboxPayload;
import com.arturk.fooddelivery.order.dto.outbox.OutboxEventEnvelope;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConverter {

    public OrderCreatedEvent toKafkaEvent(OutboxEventEnvelope<OrderCreatedEventOutboxPayload> envelope) {
        return OrderCreatedEvent.newBuilder()
                .setMetadata(EventMetadata.newBuilder()
                        .setEventId(envelope.eventId().toString())
                        .setEventType(envelope.eventType())
                        .setAggregateType(envelope.aggregateType())
                        .setAggregateId(envelope.aggregateId().toString())
                        .setOccurredAt(envelope.occurredAt())
                        .build())
                .setPayload(OrderCreatedEventPayload.newBuilder()
                        .setOrderId(envelope.payload().orderId().toString())
                        .setCustomerId(envelope.payload().customerId().toString())
                        .setRestaurantId(envelope.payload().restaurantId().toString())
                        .setStatus(com.arturk.fooddelivery.contracts.avro.order.v1.OrderStatus.valueOf(envelope.payload().status().name()))
                        .setItems(envelope.payload().items().stream()
                                .map(item -> OrderCreatedItem.newBuilder()
                                        .setMenuItemId(item.menuItemId().toString())
                                        .setQuantity(item.quantity())
                                        .build())
                                .toList())
                        .build())
                .build();
    }
}

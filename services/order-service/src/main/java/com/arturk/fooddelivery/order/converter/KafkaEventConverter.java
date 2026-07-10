package com.arturk.fooddelivery.order.converter;

import com.arturk.fooddelivery.contracts.avro.common.v1.EventMetadata;
import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedItem;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.dto.OrderCreatedEventPayload;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventConverter {

    public OrderCreatedEvent toKafkaEvent(OutboxEventEntity outboxEvent, OrderCreatedEventPayload payload) {
        return OrderCreatedEvent.newBuilder()
                .setMetadata(toMetadata(outboxEvent))
                .setPayload(com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEventPayload.newBuilder()
                        .setOrderId(payload.orderId().toString())
                        .setCustomerId(payload.customerId().toString())
                        .setRestaurantId(payload.restaurantId().toString())
                        .setStatus(com.arturk.fooddelivery.contracts.avro.order.v1.OrderStatus.valueOf(payload.status().name()))
                        .setTotalAmount(payload.totalAmount().toPlainString())
                        .setItems(payload.items().stream()
                                .map(item -> OrderCreatedItem.newBuilder()
                                        .setMenuItemId(item.menuItemId().toString())
                                        .setQuantity(item.quantity())
                                        .build())
                                .toList())
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

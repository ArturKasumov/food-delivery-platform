package com.arturk.fooddelivery.order.dto.outbox;

import java.util.UUID;

public record OutboxEventEnvelope<T>(
        UUID eventId,
        String eventType,
        String aggregateType,
        UUID aggregateId,
        String occurredAt,
        T payload
) {
}

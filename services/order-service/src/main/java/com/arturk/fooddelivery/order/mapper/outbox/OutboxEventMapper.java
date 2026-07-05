package com.arturk.fooddelivery.order.mapper.outbox;

import com.arturk.fooddelivery.order.domain.OutboxEventEntity;

public interface OutboxEventMapper<T> {

    String getEventType();

    Class<T> supportsEntityType();

    OutboxEventEntity  mapToOutboxEvent(T entity);
}

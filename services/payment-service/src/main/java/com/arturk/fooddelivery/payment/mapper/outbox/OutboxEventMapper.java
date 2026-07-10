package com.arturk.fooddelivery.payment.mapper.outbox;

import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;

public interface OutboxEventMapper<T> {

    String getEventType();

    Class<T> supportsEntityType();

    OutboxEventEntity mapToOutboxEvent(T entity);
}

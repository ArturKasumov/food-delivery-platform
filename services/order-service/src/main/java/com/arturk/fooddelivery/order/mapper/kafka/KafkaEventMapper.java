package com.arturk.fooddelivery.order.mapper.kafka;

import com.arturk.fooddelivery.order.domain.OutboxEventEntity;

public interface KafkaEventMapper {

    String getEventType();

    Object mapToKafkaEvent(OutboxEventEntity event);
}

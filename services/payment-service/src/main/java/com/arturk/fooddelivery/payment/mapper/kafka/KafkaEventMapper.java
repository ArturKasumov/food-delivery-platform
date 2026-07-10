package com.arturk.fooddelivery.payment.mapper.kafka;


import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;

public interface KafkaEventMapper {

    String getEventType();

    Object mapToKafkaEvent(OutboxEventEntity event);
}

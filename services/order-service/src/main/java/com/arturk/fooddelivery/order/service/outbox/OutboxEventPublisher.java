package com.arturk.fooddelivery.order.service.outbox;

import com.arturk.fooddelivery.order.constants.OrderEventTypes;
import com.arturk.fooddelivery.order.converter.JsonConverter;
import com.arturk.fooddelivery.order.converter.KafkaEventConverter;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.dto.outbox.OrderCreatedEventOutboxPayload;
import com.arturk.fooddelivery.order.dto.outbox.OutboxEventEnvelope;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.outbox.publisher", name = "enabled", havingValue = "true")
public class OutboxEventPublisher {

    private final OrderOutboxService outboxService;
    private final JsonConverter jsonConverter;
    private final KafkaEventConverter orderCreatedEventConverter;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.outbox.publisher.fixed-delay}")
    public void publishPendingEvents() {
        List<UUID> eventIds = outboxService.claimPublishableEventIds();

        if (CollectionUtils.isEmpty(eventIds)) {
            return;
        }

        outboxService.getProcessingEventsById(eventIds).forEach(this::publish);
    }

    private void publish(OutboxEventEntity event) {
        try {
            kafkaTemplate.send(event.getTopic(), event.getAggregateId().toString(), toKafkaEvent(event)).get();
            outboxService.markPublished(event);
            log.info("Published outbox event {} to topic {}", event.getId(), event.getTopic());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            outboxService.markFailed(event, exception);
            log.warn("Publishing outbox event {} was interrupted, mark as Failed", event.getId(), exception);
        } catch (Exception exception) {
            outboxService.markFailed(event, exception);
            log.warn("Failed to publish outbox event {}", event.getId(), exception);
        }
    }

    private Object toKafkaEvent(OutboxEventEntity event) {
        if (OrderEventTypes.ORDER_CREATED_EVENT_TYPE.equals(event.getEventType())) {
            OutboxEventEnvelope<OrderCreatedEventOutboxPayload> envelope = jsonConverter.fromJson(
                    event.getPayload(),
                    new TypeReference<>() {
                    }
            );
            return orderCreatedEventConverter.toKafkaEvent(envelope);
        }

        throw new IllegalArgumentException("Unsupported outbox event type: " + event.getEventType());
    }
}

package com.arturk.fooddelivery.order.service.outbox;

import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.mapper.kafka.KafkaEventMapperRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.outbox.publisher", name = "enabled", havingValue = "true")
public class OutboxEventPublisher {

    private final OutboxService outboxService;
    private final KafkaEventMapperRegistry kafkaEventMapperFactory;
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
        try (MDC.MDCCloseable ignored =
                     MDC.putCloseable(CorrelationIdConstants.MDC_KEY, event.getCorrelationId())) {

            try {
                ProducerRecord<String, Object> record = new ProducerRecord<>(
                        event.getTopic(),
                        event.getAggregateId().toString(),
                        getKafkaEvent(event)
                );

                record.headers().add(
                        CorrelationIdConstants.HEADER_NAME,
                        event.getCorrelationId().getBytes(StandardCharsets.UTF_8)
                );

                kafkaTemplate.send(record).get();
                outboxService.markPublished(event);
                log.info("Published outbox event {} to topic {}", event.getId(), event.getTopic());

            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();

                outboxService.markFailed(event, exception.getMessage());
                log.warn("Publishing outbox event {} was interrupted, mark as failed", event.getId(), exception);

            } catch (Exception exception) {
                outboxService.markFailed(event, exception.getMessage());
                log.warn("Failed to publish outbox event {}", event.getId(), exception);
            }
        }
    }

    private Object getKafkaEvent(OutboxEventEntity event) {
        return kafkaEventMapperFactory.getKafkaEventMapper(event.getEventType()).mapToKafkaEvent(event);
    }
}

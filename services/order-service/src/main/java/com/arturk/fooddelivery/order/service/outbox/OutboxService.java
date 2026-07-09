package com.arturk.fooddelivery.order.service.outbox;

import com.arturk.fooddelivery.order.config.properties.OutboxPublisherProperties;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import com.arturk.fooddelivery.order.mapper.outbox.OutboxEventMapperRegistry;
import com.arturk.fooddelivery.order.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxPublisherProperties outboxPublisherProperties;
    private final OutboxEventMapperRegistry outboxEventMapperRegistry;

    @Transactional
    public void saveOrderCreatedEvent(CustomerOrderEntity order) {
        OutboxEventEntity outboxEvent = outboxEventMapperRegistry.mapToOutboxEvent(
                ORDER_CREATED_EVENT_TYPE,
                order
        );
        outboxEventRepository.save(outboxEvent);
    }

    @Transactional
    public List<UUID> claimPublishableEventIds() {
        LocalDateTime processingBefore = LocalDateTime.now().minus(outboxPublisherProperties.processingTimeout());
        List<OutboxEventEntity> events = outboxEventRepository.findPublishableEventsForUpdate(
                outboxPublisherProperties.batchSize(),
                outboxPublisherProperties.maxRetryAttempts(),
                processingBefore
        );

        events.forEach(OutboxEventEntity::markProcessing);
        return events.stream()
                .map(OutboxEventEntity::getId)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OutboxEventEntity> getProcessingEventsById(List<UUID> eventIds) {
        return outboxEventRepository.findAllByIdInAndStatus(eventIds, OutboxEventStatus.PROCESSING);
    }

    public void markPublished(OutboxEventEntity event) {
        event.markPublished();
        outboxEventRepository.save(event);
    }

    public void markFailed(OutboxEventEntity event, String message) {
        event.markFailed(message);
        outboxEventRepository.save(event);
    }
}

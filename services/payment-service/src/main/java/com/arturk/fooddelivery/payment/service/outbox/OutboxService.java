package com.arturk.fooddelivery.payment.service.outbox;

import com.arturk.fooddelivery.payment.config.properties.OutboxPublisherProperties;
import com.arturk.fooddelivery.payment.domain.OutboxEventEntity;
import com.arturk.fooddelivery.payment.domain.PaymentEntity;
import com.arturk.fooddelivery.payment.enums.OutboxEventStatus;
import com.arturk.fooddelivery.payment.mapper.outbox.OutboxEventMapperRegistry;
import com.arturk.fooddelivery.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxPublisherProperties outboxPublisherProperties;
    private final OutboxEventMapperRegistry outboxEventMapperRegistry;

    @Transactional
    public void savePaymentResultEvent(PaymentEntity payment) {
        OutboxEventEntity outboxEvent = outboxEventMapperRegistry.mapToOutboxEvent(
                payment.getStatus().getResultEventType(),
                payment);

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
        int failedAttempt = event.getRetryAttempt() + 1;

        if (failedAttempt >= outboxPublisherProperties.maxRetryAttempts()) {
            event.markDead(message);
        } else {
            event.markFailed(message, LocalDateTime.now().plus(calculateRetryDelay(failedAttempt)));
        }

        outboxEventRepository.save(event);
    }

    @Transactional
    public OutboxEventEntity reprocessDeadEvent(UUID eventId) {
        OutboxEventEntity event = outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchElementException("Outbox event not found: " + eventId));

        event.reprocess();
        return event;
    }

    private Duration calculateRetryDelay(int failedAttempt) {
        Duration delay = outboxPublisherProperties.retryInitialDelay();
        for (int attempt = 1; attempt < failedAttempt; attempt++) {
            delay = delay.multipliedBy(2);
        }
        return delay;
    }
}

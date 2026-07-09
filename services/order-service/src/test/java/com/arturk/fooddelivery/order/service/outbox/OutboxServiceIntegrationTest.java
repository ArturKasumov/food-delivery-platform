package com.arturk.fooddelivery.order.service.outbox;

import com.arturk.fooddelivery.order.AbstractIntegrationTest;
import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.order.domain.CustomerOrderEntity;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import com.arturk.fooddelivery.order.repository.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class OutboxServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        outboxEventRepository.deleteAll();
        MDC.put(CorrelationIdConstants.MDC_KEY, UUID.randomUUID().toString());
    }

    @Test
    void saveOrderCreatedEventSuccessfully() {
        // given
        CustomerOrderEntity order = createCustomerOrderEntity();

        // when
        outboxService.saveOrderCreatedEvent(order);

        // then
        assertThat(outboxEventRepository.findAll())
                .filteredOn(event -> event.getAggregateId().equals(order.getId()))
                .filteredOn(event -> event.getEventType().equals(ORDER_CREATED_EVENT_TYPE))
                .singleElement()
                .satisfies(event -> assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING));
    }

    @Test
    void claimPublishableEventIds_shouldClaimOnlyPendingEvents() {
        // given
        OutboxEventEntity pendingEvent1 = saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PENDING));
        OutboxEventEntity pendingEvent2 = saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PENDING));
        saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PROCESSING));
        saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PUBLISHED));

        // when
        List<UUID> ids = outboxService.claimPublishableEventIds();

        // then
        assertThat(ids).containsExactly(pendingEvent1.getId(), pendingEvent2.getId());

        List<OutboxEventEntity> claimedEvents = outboxEventRepository.findAllById(ids);

        assertThat(claimedEvents)
                .extracting(OutboxEventEntity::getStatus)
                .containsOnly(OutboxEventStatus.PROCESSING);
    }

    @Test
    void claimPublishableEventIds_shouldClaimFailedEventsWhenRetryAttemptsNotExceeded() {
        // given
        OutboxEventEntity failedEvent = saveOutboxEvent(createFailedOutboxEvent(1));

        // when
        List<UUID> ids = outboxService.claimPublishableEventIds();

        // then
        assertThat(ids).containsExactly(failedEvent.getId());

        OutboxEventEntity event = outboxEventRepository.findById(failedEvent.getId()).orElseThrow();
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PROCESSING);
    }

    @Test
    void claimPublishableEventIds_shouldNotClaimEventWhenRetryAttemptsExceeded() {
        // given
        saveOutboxEvent(createFailedOutboxEvent(outboxPublisherProperties.maxRetryAttempts()));

        // when
        List<UUID> ids = outboxService.claimPublishableEventIds();

        // then
        assertThat(ids).isEmpty();
    }

    @Test
    void claimPublishableEventIds_shouldReclaimStuckProcessingEventAfterTimeout() {
        // given
        OutboxEventEntity stuckEvent = createOutboxEvent(OutboxEventStatus.PROCESSING);
        saveOutboxEvent(stuckEvent);
        setUpdatedAt(
                stuckEvent.getId(),
                LocalDateTime.now().minus(outboxPublisherProperties.processingTimeout()).minusMinutes(1)
        );

        // when
        List<UUID> ids = outboxService.claimPublishableEventIds();

        // then
        assertThat(ids).containsExactly(stuckEvent.getId());

        OutboxEventEntity event = outboxEventRepository.findById(stuckEvent.getId()).orElseThrow();
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PROCESSING);
    }

    @Test
    void claimPublishableEventIds_shouldNotClaimFreshProcessingEvent() {
        // given
        OutboxEventEntity stuckEvent = createOutboxEvent(OutboxEventStatus.PROCESSING);
        saveOutboxEvent(stuckEvent);
        setUpdatedAt(
                stuckEvent.getId(),
                LocalDateTime.now().minus(outboxPublisherProperties.processingTimeout()).plusMinutes(1)
        );

        // when
        List<UUID> ids = outboxService.claimPublishableEventIds();

        // then
        assertThat(ids).isEmpty();
    }

    @Test
    void getProcessingEventsById_shouldReturnOnlyProcessingEvents() {
        // given
        OutboxEventEntity processing = saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PROCESSING));
        OutboxEventEntity pending = saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PENDING));
        OutboxEventEntity published = saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PUBLISHED));

        List<UUID> ids = List.of(
                processing.getId(),
                pending.getId(),
                published.getId()
        );

        // when
        List<OutboxEventEntity> result = outboxService.getProcessingEventsById(ids);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(processing.getId());
    }

    @Test
    void markPublished_shouldChangeStatusToPublished() {
        // given
        OutboxEventEntity event = saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PROCESSING));

        // when
        outboxService.markPublished(event);

        // then
        OutboxEventEntity updated = outboxEventRepository.findById(event.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
        assertThat(updated.getPublishedAt()).isNotNull();
        assertThat(updated.getError()).isNull();
    }

    @Test
    void markFailed_shouldChangeStatusToFailedAndSaveErrorMessage() {
        // given
        OutboxEventEntity event = saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PROCESSING));
        Exception exception = new RuntimeException("Error message");

        // when
        outboxService.markFailed(event, exception.getMessage());

        // then
        OutboxEventEntity updated = outboxEventRepository.findById(event.getId()).orElseThrow();

        assertThat(updated.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(updated.getError()).isEqualTo("Error message");
        assertThat(updated.getRetryAttempt()).isEqualTo(1);
    }

    @Test
    void findPublishableEventsForUpdate_shouldSkipLockedRows() throws Exception {
        // given
        for (int i = 0; i < 4; i++) {
            saveOutboxEvent(createOutboxEvent(OutboxEventStatus.PENDING));
        }

        CountDownLatch firstTransactionLockedRows = new CountDownLatch(1);
        CountDownLatch allowFirstTransactionToCommit = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<List<UUID>> firstTx = executor.submit(() ->
                transactionTemplate.execute(status -> {
                    List<OutboxEventEntity> events =
                            outboxEventRepository.findPublishableEventsForUpdate(
                                    2,
                                    3,
                                    LocalDateTime.now().minusMinutes(10)
                            );

                    firstTransactionLockedRows.countDown();

                    try {
                        allowFirstTransactionToCommit.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    return events.stream()
                            .map(OutboxEventEntity::getId)
                            .toList();
                })
        );

        firstTransactionLockedRows.await();

        Future<List<UUID>> secondTx = executor.submit(() ->
                transactionTemplate.execute(status -> {
                    List<OutboxEventEntity> events =
                            outboxEventRepository.findPublishableEventsForUpdate(
                                    2,
                                    3,
                                    LocalDateTime.now().minusMinutes(10)
                            );

                    return events.stream()
                            .map(OutboxEventEntity::getId)
                            .toList();
                })
        );

        List<UUID> ids2 = secondTx.get();

        allowFirstTransactionToCommit.countDown();

        List<UUID> ids1 = firstTx.get();

        assertThat(ids2).isNotEmpty();
        assertThat(ids1)
                .isNotEmpty()
                .doesNotContainAnyElementsOf(ids2);

        executor.shutdown();
    }

    private CustomerOrderEntity createCustomerOrderEntity() {
        return new CustomerOrderEntity(UUID.randomUUID(), UUID.randomUUID());
    }

    private OutboxEventEntity createOutboxEvent(OutboxEventStatus status) {
        OutboxEventEntity event = new OutboxEventEntity(
                UUID.randomUUID(),
                ORDER_AGGREGATE_TYPE,
                UUID.randomUUID(),
                ORDER_CREATED_EVENT_TYPE,
                UUID.randomUUID().toString(),
                kafkaTopicsProperties.orderEvents(),
                "{}"
        );
        event.setStatus(status);
        return event;
    }

    private OutboxEventEntity createFailedOutboxEvent(int retryAttempt) {
        OutboxEventEntity event = createOutboxEvent(OutboxEventStatus.FAILED);
        event.setRetryAttempt(retryAttempt);
        return event;
    }

    private OutboxEventEntity saveOutboxEvent(OutboxEventEntity event) {
        return outboxEventRepository.save(event);
    }

    private void setUpdatedAt(UUID eventId, LocalDateTime updatedAt) {
        jdbcTemplate.update(
                "UPDATE outbox_events SET updated_at = ? WHERE id = ?",
                updatedAt,
                eventId
        );
    }
}

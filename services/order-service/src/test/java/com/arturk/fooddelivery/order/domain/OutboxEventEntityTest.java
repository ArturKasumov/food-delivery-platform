package com.arturk.fooddelivery.order.domain;

import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventEntityTest {

    @Test
    void newOutboxEventEntity() {
        OutboxEventEntity event = getOutboxEvent();

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(event.getRetryAttempt()).isZero();
        assertThat(event.getNextAttemptAt()).isNotNull();
    }

    @Test
    void markOutboxEventAsProcessing() {
        OutboxEventEntity event = getOutboxEvent();

        event.markProcessing();

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PROCESSING);
        assertThat(event.getError()).isNull();
    }

    @Test
    void markOutboxEventAsPublished() {
        OutboxEventEntity event = getOutboxEvent();

        event.markPublished();

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
        assertThat(event.getPublishedAt()).isNotNull();
        assertThat(event.getNextAttemptAt()).isNull();
        assertThat(event.getError()).isNull();
    }

    @Test
    void markOutboxEventAsFailed() {
        OutboxEventEntity event = getOutboxEvent();
        LocalDateTime nextAttemptAt = LocalDateTime.now().plusSeconds(5);

        event.markFailed("Testing failure", nextAttemptAt);

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(event.getRetryAttempt()).isEqualTo(1);
        assertThat(event.getError()).isEqualTo("Testing failure");
        assertThat(event.getNextAttemptAt()).isEqualTo(nextAttemptAt);
    }

    @Test
    void markOutboxEventAsDead() {
        OutboxEventEntity event = getOutboxEvent();

        event.markDead("Terminal failure");

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.DEAD);
        assertThat(event.getRetryAttempt()).isEqualTo(1);
        assertThat(event.getError()).isEqualTo("Terminal failure");
        assertThat(event.getNextAttemptAt()).isNull();
    }

    @Test
    void reprocessDeadOutboxEvent() {
        OutboxEventEntity event = getOutboxEvent();
        event.markDead("Terminal failure");

        event.reprocess();

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(event.getRetryAttempt()).isZero();
        assertThat(event.getError()).isEqualTo("Terminal failure");
        assertThat(event.getNextAttemptAt()).isBefore(LocalDateTime.now());
    }

    private OutboxEventEntity getOutboxEvent() {
        return new OutboxEventEntity(
                UUID.randomUUID(),
                ORDER_AGGREGATE_TYPE,
                UUID.randomUUID(),
                "OrderCreatedEvent",
                UUID.randomUUID().toString(),
                "test.orders.events",
                "{}"
        );
    }
}

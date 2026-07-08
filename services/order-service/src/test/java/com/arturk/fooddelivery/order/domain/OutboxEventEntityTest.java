package com.arturk.fooddelivery.order.domain;

import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventEntityTest {

    @Test
    void newOutboxEventEntity() {
        OutboxEventEntity event = getOutboxEvent();

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(event.getRetryAttempt()).isZero();
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
        assertThat(event.getError()).isNull();
    }

    @Test
    void markOutboxEventAsFailed() {
        OutboxEventEntity event = getOutboxEvent();

        event.markFailed("Testing failure");

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
        assertThat(event.getRetryAttempt()).isEqualTo(1);
        assertThat(event.getError()).isEqualTo("Testing failure");
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

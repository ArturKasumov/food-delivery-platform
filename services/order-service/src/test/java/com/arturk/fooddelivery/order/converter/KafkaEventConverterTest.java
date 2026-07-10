package com.arturk.fooddelivery.order.converter;

import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedItem;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.messaging.outbox.OrderCreatedEventPayload;
import com.arturk.fooddelivery.order.messaging.outbox.OrderItemCreatedEventPayload;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class KafkaEventConverterTest {

    private final KafkaEventConverter kafkaEventConverter = new KafkaEventConverter();

    @Test
    void shouldConvertOrderCreatedOutboxEventToKafkaEvent() {
        UUID eventId = UUID.randomUUID();
        UUID aggregateId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();

        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 9, 10, 30);

        OutboxEventEntity outboxEvent = new OutboxEventEntity();
        outboxEvent.setId(eventId);
        outboxEvent.setAggregateId(aggregateId);
        outboxEvent.setCorrelationId(correlationId.toString());
        outboxEvent.setAggregateType(ORDER_AGGREGATE_TYPE);
        outboxEvent.setEventType(ORDER_CREATED_EVENT_TYPE);
        outboxEvent.setCreatedAt(createdAt);

        OrderCreatedEventPayload payload = new OrderCreatedEventPayload(
                orderId,
                customerId,
                restaurantId,
                OrderStatus.PENDING_PAYMENT,
                new BigDecimal("250.00"),
                List.of(
                        new OrderItemCreatedEventPayload(
                                menuItemId,
                                2
                        )
                )
        );

        OrderCreatedEvent result = kafkaEventConverter.toKafkaEvent(outboxEvent, payload);

        assertThat(result).isNotNull();

        assertThat(result.getMetadata()).isNotNull();
        assertThat(result.getMetadata().getEventId()).isEqualTo(eventId.toString());
        assertThat(result.getMetadata().getEventType()).isEqualTo(ORDER_CREATED_EVENT_TYPE);
        assertThat(result.getMetadata().getAggregateType()).isEqualTo(ORDER_AGGREGATE_TYPE);
        assertThat(result.getMetadata().getAggregateId()).isEqualTo(aggregateId.toString());
        assertThat(result.getMetadata().getCorrelationId()).isEqualTo(correlationId.toString());
        assertThat(result.getMetadata().getOccurredAt()).isEqualTo(createdAt.toString());

        assertThat(result.getPayload()).isNotNull();
        assertThat(result.getPayload().getOrderId()).isEqualTo(orderId.toString());
        assertThat(result.getPayload().getCustomerId()).isEqualTo(customerId.toString());
        assertThat(result.getPayload().getRestaurantId()).isEqualTo(restaurantId.toString());
        assertThat(result.getPayload().getStatus().name()).isEqualTo(OrderStatus.PENDING_PAYMENT.name());
        assertThat(result.getPayload().getTotalAmount()).isEqualTo("250.00");

        assertThat(result.getPayload().getItems()).hasSize(1);

        OrderCreatedItem resultItem = result.getPayload().getItems().getFirst();

        assertThat(resultItem.getMenuItemId()).isEqualTo(menuItemId.toString());
        assertThat(resultItem.getQuantity()).isEqualTo(2);
    }
}

package com.arturk.fooddelivery.order.service.outbox;

import com.arturk.fooddelivery.contracts.avro.order.v1.OrderCreatedEvent;
import com.arturk.fooddelivery.order.AbstractIntegrationTest;
import com.arturk.fooddelivery.order.constants.CorrelationIdConstants;
import com.arturk.fooddelivery.order.converter.JsonConverter;
import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.enums.OrderStatus;
import com.arturk.fooddelivery.order.enums.OutboxEventStatus;
import com.arturk.fooddelivery.order.messaging.outbox.OrderCreatedEventPayload;
import com.arturk.fooddelivery.order.messaging.outbox.OrderItemCreatedEventPayload;
import com.arturk.fooddelivery.order.repository.OutboxEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.header.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventPublisherIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JsonConverter jsonConverter;

    @Autowired
    private OutboxEventPublisher outboxEventPublisher;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @BeforeEach
    void setUp() {
        outboxEventRepository.deleteAll();
    }

    @Test
    void publishPendingEvents_sendsEventToKafkaAndMarksOutboxEventPublished() {
        //given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        String correlationId = UUID.randomUUID().toString();

        OutboxEventEntity event = outboxEventRepository.save(new OutboxEventEntity(
                UUID.randomUUID(),
                ORDER_AGGREGATE_TYPE,
                orderId,
                ORDER_CREATED_EVENT_TYPE,
                correlationId,
                kafkaTopicsProperties.orderEvents(),
                jsonConverter.toJson(new OrderCreatedEventPayload(
                        orderId,
                        customerId,
                        restaurantId,
                        OrderStatus.PENDING_PAYMENT,
                        List.of(new OrderItemCreatedEventPayload(menuItemId, 2))
                ))
        ));

        try (KafkaConsumer<String, Object> consumer = kafkaTestClient.getKafkaConsumer()) {
            kafkaTestClient.subscribeFromEnd(consumer, kafkaTopicsProperties.orderEvents());

            //when
            outboxEventPublisher.publishPendingEvents();

            //then
            ConsumerRecord<String, Object> record = kafkaTestClient.pollKafkaEventByEventId(
                    consumer,
                    event.getId(),
                    Duration.ofSeconds(kafkaTestClient.RECORD_TIMEOUT_SECONDS)
            );

            assertThat(record.key()).isEqualTo(orderId.toString());
            Header correlationHeader = record.headers().lastHeader(CorrelationIdConstants.HEADER_NAME);
            assertThat(correlationHeader).isNotNull();
            assertThat(new String(correlationHeader.value(), StandardCharsets.UTF_8)).isEqualTo(correlationId);
            assertThat(record.value()).isInstanceOf(OrderCreatedEvent.class);

            OrderCreatedEvent orderCreatedEvent = (OrderCreatedEvent) record.value();
            assertThat(orderCreatedEvent.getMetadata().getEventId()).isEqualTo(event.getId().toString());
            assertThat(orderCreatedEvent.getMetadata().getAggregateId()).isEqualTo(orderId.toString());
            assertThat(orderCreatedEvent.getPayload().getCustomerId()).isEqualTo(customerId.toString());
            assertThat(orderCreatedEvent.getPayload().getRestaurantId()).isEqualTo(restaurantId.toString());
            assertThat(orderCreatedEvent.getPayload().getStatus().name()).isEqualTo(OrderStatus.PENDING_PAYMENT.name());
            assertThat(orderCreatedEvent.getPayload().getItems())
                    .hasSize(1)
                    .first()
                    .satisfies(item -> {
                        assertThat(item.getMenuItemId()).isEqualTo(menuItemId.toString());
                        assertThat(item.getQuantity()).isEqualTo(2);
                    });
        }

        OutboxEventEntity publishedEvent = outboxEventRepository.findById(event.getId()).orElseThrow();
        assertThat(publishedEvent.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
        assertThat(publishedEvent.getPublishedAt()).isNotNull();
    }
}

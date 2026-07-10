package com.arturk.fooddelivery.order.service.scheduler;

import com.arturk.fooddelivery.order.domain.OutboxEventEntity;
import com.arturk.fooddelivery.order.mapper.kafka.KafkaEventMapperRegistry;
import com.arturk.fooddelivery.order.mapper.kafka.KafkaOrderCreatedEventMapper;
import com.arturk.fooddelivery.order.service.outbox.OutboxService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_AGGREGATE_TYPE;
import static com.arturk.fooddelivery.order.constants.OrderEventTypes.ORDER_CREATED_EVENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

    @Mock
    private OutboxService outboxService;

    @Mock
    private KafkaEventMapperRegistry kafkaEventMapperRegistry;

    @Mock
    private KafkaOrderCreatedEventMapper kafkaEventMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private OutboxEventPublisher outboxEventPublisher;

    @Test
    void publishPendingEventsSuccessfully() {
        //given
        OutboxEventEntity event = createOutboxEvent();
        Object kafkaEvent = new Object();

        when(outboxService.claimPublishableEventIds()).thenReturn(List.of(event.getId()));
        when(outboxService.getProcessingEventsById(List.of(event.getId()))).thenReturn(List.of(event));
        when(kafkaEventMapperRegistry.getKafkaEventMapper(ORDER_CREATED_EVENT_TYPE)).thenReturn(kafkaEventMapper);
        when(kafkaEventMapper.mapToKafkaEvent(event)).thenReturn(kafkaEvent);
        when(kafkaTemplate.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        //when
        outboxEventPublisher.publishPendingEvents();

        // then
        verify(outboxService, times(1)).markPublished(event);
        verify(outboxService, never()).markFailed(any(), any());
    }

    @Test
    void publishPendingEventsWhenKafkaSendFails() {
        //given
        OutboxEventEntity event = createOutboxEvent();
        Object kafkaEvent = new Object();

        when(outboxService.claimPublishableEventIds()).thenReturn(List.of(event.getId()));
        when(outboxService.getProcessingEventsById(List.of(event.getId()))).thenReturn(List.of(event));
        when(kafkaEventMapperRegistry.getKafkaEventMapper(ORDER_CREATED_EVENT_TYPE)).thenReturn(kafkaEventMapper);
        when(kafkaEventMapper.mapToKafkaEvent(event)).thenReturn(kafkaEvent);
        when(kafkaTemplate.send(any(ProducerRecord.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("broker down")));

        //when
        outboxEventPublisher.publishPendingEvents();

        //then
        ArgumentCaptor<String> errorMessageCaptor = ArgumentCaptor.forClass(String.class);
        verify(outboxService, times(1)).markFailed(eq(event), errorMessageCaptor.capture());
        assertThat(errorMessageCaptor.getValue()).contains("broker down");
        verify(outboxService, never()).markPublished(any());
    }

    private static OutboxEventEntity createOutboxEvent() {
        return new OutboxEventEntity(
                UUID.randomUUID(),
                ORDER_AGGREGATE_TYPE,
                UUID.randomUUID(),
                ORDER_CREATED_EVENT_TYPE,
                UUID.randomUUID().toString(),
                "test.orders.events",
                "{}"
        );
    }
}
